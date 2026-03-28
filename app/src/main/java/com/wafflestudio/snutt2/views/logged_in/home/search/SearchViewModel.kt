package com.wafflestudio.snutt2.views.logged_in.home.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.wafflestudio.snutt2.RemoteConfig
import com.wafflestudio.snutt2.data.bookmark.BookmarkRepository
import com.wafflestudio.snutt2.data.current_table.CurrentTableLectureRepository
import com.wafflestudio.snutt2.data.lecture_info.LectureInfoRepository
import com.wafflestudio.snutt2.data.lecture_search.LectureSearchRepository
import com.wafflestudio.snutt2.data.tables.TableRepository
import com.wafflestudio.snutt2.data.table_display.TableDisplayRepository
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.data.vacancy_noti.VacancyRepository
import com.wafflestudio.snutt2.domain.GetCurrentTableThemeUseCase
import com.wafflestudio.snutt2.domainmodel.BuiltInTheme
import com.wafflestudio.snutt2.domainmodel.CourseBook
import com.wafflestudio.snutt2.domainmodel.LocalLecture
import com.wafflestudio.snutt2.domainmodel.SearchTag
import com.wafflestudio.snutt2.domainmodel.SearchTag.Companion.etcTags
import com.wafflestudio.snutt2.domainmodel.SearchTag.Companion.timeTags
import com.wafflestudio.snutt2.domainmodel.SearchedLecture
import com.wafflestudio.snutt2.domainmodel.SyllabusLecture
import com.wafflestudio.snutt2.domainmodel.TableLectureCustom
import com.wafflestudio.snutt2.domainmodel.TableTheme
import com.wafflestudio.snutt2.domainmodel.TableTrimParam
import com.wafflestudio.snutt2.domainmodel.flatMapToSearchTime
import com.wafflestudio.snutt2.lib.DataWithState
import com.wafflestudio.snutt2.lib.Selectable
import com.wafflestudio.snutt2.lib.concatenate
import com.wafflestudio.snutt2.lib.getFittingTrimParam
import com.wafflestudio.snutt2.lib.logging.AddToBookmarkParameter
import com.wafflestudio.snutt2.lib.logging.AddToVacancyParameter
import com.wafflestudio.snutt2.lib.logging.AnalyticsEvent
import com.wafflestudio.snutt2.lib.logging.AnalyticsLogger
import com.wafflestudio.snutt2.lib.logging.DetailScreenReferrer
import com.wafflestudio.snutt2.lib.logging.LectureActionReferrer
import com.wafflestudio.snutt2.lib.logging.SearchLectureParameter
import com.wafflestudio.snutt2.lib.network.AuthError
import com.wafflestudio.snutt2.lib.network.DisplayMessageResolver
import com.wafflestudio.snutt2.lib.network.DomainError
import com.wafflestudio.snutt2.lib.network.LectureOverlap
import com.wafflestudio.snutt2.lib.network.dto.core.LectureBuildingDto
import com.wafflestudio.snutt2.lib.network.onFailure
import com.wafflestudio.snutt2.lib.network.onSuccess
import com.wafflestudio.snutt2.lib.toDataWithState
import com.wafflestudio.snutt2.model.TagType
import com.wafflestudio.snutt2.views.logged_in.home.search.search_option.clusterToTimeBlocks
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val currentTableLectureRepository: CurrentTableLectureRepository,
    private val tableRepository: TableRepository,
    private val lectureSearchRepository: LectureSearchRepository,
    private val lectureInfoRepository: LectureInfoRepository,
    private val vacancyRepository: VacancyRepository,
    private val bookmarkRepository: BookmarkRepository,
    private val tableDisplayRepository: TableDisplayRepository,
    private val userRepository: UserRepository,
    private val displayMessageResolver: DisplayMessageResolver,
    private val analyticsLogger: AnalyticsLogger,
    private val getCurrentTableThemeUseCase: GetCurrentTableThemeUseCase,
    private val remoteConfig: RemoteConfig,
) : ViewModel() {

    private val _querySignal = MutableSharedFlow<Unit>(replay = 0)

    val accessToken get() = userRepository.accessToken

    private val _uiEvent = MutableSharedFlow<SearchUiEvent>(replay = 0)
    val uiEvent = _uiEvent.asSharedFlow()

    private val _uiState: MutableStateFlow<SearchUiState> = MutableStateFlow(
        checkNotNull(tableRepository.currentTable.value).let { table ->
            SearchUiState(
                pageMode = PageMode.Search,
                courseBook = table.summary.courseBook,
                selectedLecture = null,
                currentTableLectures = table.lectures,
                tableTrimParam = table.lectures.getFittingTrimParam(tableDisplayRepository.tableTrimParam.value),
                tableLectureCustomOptions = tableDisplayRepository.tableLectureCustomOption.value,
                tableTheme = BuiltInTheme.SNUTT,
                isCompactMode = tableDisplayRepository.compactMode.value,
                firstBookmarkAlert = bookmarkRepository.firstBookmarkAlert.value,
                bookmarks = bookmarkRepository.bookmarks.value[table.summary.courseBook] ?: emptyList(),
                vacancyList = vacancyRepository.vacancyLectures.value,
                disableMapFeature = true,
                bottomSheetType = SearchUiState.BottomSheetType.None,
                dialogState = SearchUiState.DialogState.None,
                searchTitle = "",
                selectedTags = emptyList(),
                searchResultListState = SearchResultListState.PLACEHOLDER,
                tagTypes = emptyList(),
                selectedTagType = TagType.SORT_CRITERIA,
                allSearchTags = emptyList(),
                searchTags = emptyList(),
                recentSearchedDepartments = emptyList(),
                draggedTimeBlock = TableTrimParam.TimeBlockGridDefault,
            )
        },
    )
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    // PagingData는 UiState에 통합 불가 - 별도 StateFlow로 유지
    val queryResults: StateFlow<PagingData<DataWithState<SearchedLecture, LectureState>>> = combine(
        _querySignal.flatMapLatest {
            combine(
                tableRepository.currentTable.filterNotNull(),
                uiState.filter { it.pageMode == PageMode.Search },
                ::Pair,
            ).take(1).flatMapLatest { (table, state) ->
                lectureSearchRepository.getLectureSearchResultStream(
                    year = table.summary.courseBook.year,
                    semester = table.summary.courseBook.semester,
                    title = state.searchTitle,
                    tags = state.selectedTags,
                    times = state.draggedTimeBlock.clusterToTimeBlocks().ifEmpty { null },
                    timesToExclude = if (state.selectedTags.contains(SearchTag.TimeEmpty)) {
                        table.lectures.flatMapToSearchTime()
                    } else {
                        null
                    },
                ).cachedIn(viewModelScope)
            }
        },
        tableRepository.currentTable.filterNotNull(),
        uiState.filter { it.pageMode == PageMode.Search },
    ) { pagingDataResult, table, state ->
        pagingDataResult.map { searchedLecture ->
            searchedLecture.toDataWithState(
                LectureState(
                    selected = searchedLecture.id == state.selectedLecture?.id,
                    contained = table.lectures
                        .filterIsInstance<SyllabusLecture>()
                        .any { lecture ->
                            lecture.originalLectureId == searchedLecture.id
                        },
                    isBookmarked = state.bookmarks.any { it.id == searchedLecture.id },
                    isVacancyRegistered = state.vacancyList.any { it.id == searchedLecture.id },
                ),
            )
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, PagingData.empty())

    init {
        viewModelScope.launch {
            combine(
                tableRepository.currentTable.filterNotNull(),
                combine(
                    tableDisplayRepository.tableTrimParam,
                    tableDisplayRepository.tableLectureCustomOption,
                    tableDisplayRepository.compactMode,
                    ::Triple,
                ),
                combine(
                    bookmarkRepository.firstBookmarkAlert,
                    lectureSearchRepository.recentSearchedDepartmentTags,
                    getCurrentTableThemeUseCase(),
                    ::Triple,
                ),
                combine(
                    vacancyRepository.vacancyLectures,
                    tableRepository.currentTable
                        .filterNotNull()
                        .distinctUntilChanged { o, n -> o.summary.courseBook == n.summary.courseBook }
                        .flatMapLatest { table ->
                            val courseBook = table.summary.courseBook
                            flow {
                                emit(emptyList<SearchedLecture>())
                                bookmarkRepository.fetchBookmarks(courseBook)
                                emitAll(bookmarkRepository.bookmarks.map { it[courseBook] ?: emptyList() })
                            }
                        },
                    remoteConfig.disableMapFeature,
                    ::Triple,
                ),
                // C-2: 초기 로드 + 학기 변경 시 재조회
                tableRepository.currentTable
                    .filterNotNull()
                    .distinctUntilChanged { o, n -> o.summary.courseBook == n.summary.courseBook }
                    .flatMapLatest { table ->
                        flow {
                            try {
                                emit(lectureSearchRepository.getSearchTags(table.summary.courseBook.year, table.summary.courseBook.semester))
                            } catch (_: Exception) {
                                emit(emptyList())
                            }
                        }
                    },
            ) { table, (trimParam, lectureCustom, compact), (firstAlert, recentDepts, theme), (vacancy, bookmarks, disableMap), searchTags ->
                val prevState = _uiState.value
                val courseBook = table.summary.courseBook
                val courseBookChanged = prevState.courseBook != courseBook

                val allTags = searchTags + etcTags + timeTags
                val tagTypes = allTags.map { it.type }.toSet().toList()

                _uiState.update { current ->
                    val selectedTagType = if (tagTypes.contains(current.selectedTagType)) current.selectedTagType else TagType.SORT_CRITERIA
                    var next = current.copy(
                        courseBook = courseBook,
                        currentTableLectures = table.lectures,
                        tableTrimParam = (table.lectures + listOfNotNull(current.selectedLecture))
                            .getFittingTrimParam(trimParam),
                        tableLectureCustomOptions = lectureCustom,
                        tableTheme = theme,
                        isCompactMode = compact,
                        firstBookmarkAlert = firstAlert,
                        bookmarks = bookmarks,
                        vacancyList = vacancy,
                        disableMapFeature = disableMap,
                        tagTypes = tagTypes,
                        selectedTagType = selectedTagType,
                        allSearchTags = allTags,
                        searchTags = buildSelectableTags(allTags, selectedTagType, current.selectedTags),
                        recentSearchedDepartments = buildRecentDepts(recentDepts, allTags, current.selectedTags),
                    )
                    if (courseBookChanged) {
                        next = next.copy(
                            pageMode = PageMode.Search,
                            selectedLecture = null,
                            tableTrimParam = table.lectures.getFittingTrimParam(trimParam),
                            bottomSheetType = SearchUiState.BottomSheetType.None,
                            dialogState = SearchUiState.DialogState.None,
                            searchTitle = "",
                            selectedTags = emptyList(),
                            searchResultListState = SearchResultListState.PLACEHOLDER,
                            selectedTagType = TagType.SORT_CRITERIA,
                            searchTags = buildSelectableTags(allTags, TagType.SORT_CRITERIA, emptyList()),
                            recentSearchedDepartments = buildRecentDepts(recentDepts, allTags, emptyList()),
                            draggedTimeBlock = TableTrimParam.TimeBlockGridDefault,
                        )
                    }
                    next
                }
            }.collect()
        }

        // B-2: vacancy 초기 로드
        viewModelScope.launch { vacancyRepository.fetchVacancyLectures() }
    }

    // region Public methods

    fun setTitle(title: String) {
        _uiState.update { it.copy(searchTitle = title.replace("\n", "")) }
    }

    fun onSearch() {
        viewModelScope.launch {
            _uiState.update { it.copy(searchResultListState = SearchResultListState.HAS_RESULTS) }
            query()
        }
    }

    fun onClearEditText() {
        _uiState.update { it.copy(searchTitle = "") }
    }

    fun setTagType(tagType: TagType) {
        _uiState.update { current ->
            current.copy(
                selectedTagType = tagType,
                searchTags = buildSelectableTags(current.allSearchTags, tagType, current.selectedTags),
            )
        }
    }

    fun onToggleTag(tag: SearchTag) {
        _uiState.update { current ->
            val newSelectedTags = if (current.selectedTags.contains(tag)) {
                current.selectedTags.filter { it != tag }
            } else {
                if (tag.type.isExclusive) {
                    concatenate(current.selectedTags.filter { it.type != tag.type }, listOf(tag))
                } else {
                    concatenate(current.selectedTags, listOf(tag))
                }
            }
            current.copy(
                selectedTags = newSelectedTags,
                searchTags = current.searchTags.map { it.copy(state = newSelectedTags.contains(it.item)) },
                recentSearchedDepartments = current.recentSearchedDepartments.map {
                    it.copy(state = newSelectedTags.contains(it.item))
                },
            )
        }
    }

    fun onToggleTagAndQuery(tag: SearchTag) {
        onToggleTag(tag)
        onSearch()
    }

    fun onTimeSelectCancel() {
        val state = _uiState.value
        if (state.draggedTimeBlock.all { row -> row.all { cell -> !cell } }) {
            onToggleTag(SearchTag.TimeSelect)
        }
    }

    fun onTimeSelectConfirm(draggedTimeBlock: List<List<Boolean>>) {
        _uiState.update { current ->
            val newTags = if (draggedTimeBlock.all { row -> row.all { cell -> !cell } }) {
                current.selectedTags.filter { it != SearchTag.TimeSelect }
            } else {
                current.selectedTags
            }
            current.copy(
                draggedTimeBlock = draggedTimeBlock,
                selectedTags = newTags,
                searchTags = current.searchTags.map { it.copy(state = newTags.contains(it.item)) },
                recentSearchedDepartments = current.recentSearchedDepartments.map {
                    it.copy(state = newTags.contains(it.item))
                },
            )
        }
    }

    fun storeRecentSearchedDepartments() {
        _uiState.value.selectedTags
            .filter { it.type == TagType.DEPARTMENT }
            .forEach { lectureSearchRepository.storeRecentSearchedDepartment(it) }
    }

    fun removeRecentSearchedDepartment(tag: SearchTag) {
        lectureSearchRepository.removeRecentSearchedDepartment(tag)
    }

    fun onToggleLectureSelection(lecture: SearchedLecture) {
        _uiState.update { current ->
            val newSelection = if (lecture.id == current.selectedLecture?.id) null else lecture
            current.copy(selectedLecture = newSelection)
        }
    }

    fun onClickBookmark(lecture: SearchedLecture, isBookmarked: Boolean) {
        viewModelScope.launch {
            if (isBookmarked) {
                _uiState.update { it.copy(dialogState = SearchUiState.DialogState.ConfirmDeleteBookmark(lecture)) }
            } else {
                analyticsLogger.logEvent(
                    AnalyticsEvent.AddToBookmark(
                        AddToBookmarkParameter(
                            lectureId = lecture.id,
                            referrer = LectureActionReferrer.Search(_uiState.value.searchTitle),
                        ),
                    ),
                )
                val courseBook = tableRepository.currentTable.value?.summary?.courseBook ?: return@launch
                bookmarkRepository.addBookmark(courseBook, lecture).onFailure { handleSearchError(it); return@launch }

                if (bookmarkRepository.firstBookmarkAlert.value) {
                    bookmarkRepository.setFirstBookmarkAlertShown()
                    _uiEvent.emit(SearchUiEvent.ShowSnackBar(SearchUiEvent.ShowSnackBar.SearchSnackBarEvent.FIRST_BOOKMARK_ADD))
                }
            }
        }
    }

    fun confirmDeleteBookmark(lecture: SearchedLecture) {
        viewModelScope.launch {
            _uiState.update { it.copy(dialogState = SearchUiState.DialogState.None) }
            val courseBook = tableRepository.currentTable.value?.summary?.courseBook ?: return@launch
            bookmarkRepository.deleteBookmark(courseBook, lecture)
                .onFailure { handleSearchError(it); return@launch }
        }
    }

    fun onClickVacancy(lecture: SearchedLecture, isVacancyRegistered: Boolean) {
        viewModelScope.launch {
            if (isVacancyRegistered) {
                _uiState.update { it.copy(dialogState = SearchUiState.DialogState.ConfirmDeleteVacancy(lecture)) }
            } else {
                analyticsLogger.logEvent(
                    AnalyticsEvent.AddToVacancy(
                        AddToVacancyParameter(
                            lectureId = lecture.id,
                            referrer = LectureActionReferrer.Search(_uiState.value.searchTitle),
                        ),
                    ),
                )
                vacancyRepository.addVacancyLecture(lecture).onFailure { handleSearchError(it); return@launch }
                if (vacancyRepository.firstVacancyAdd.value) {
                    vacancyRepository.setVacancyAdded()
                    _uiEvent.emit(SearchUiEvent.ShowSnackBar(SearchUiEvent.ShowSnackBar.SearchSnackBarEvent.FIRST_VACANCY_ADD))
                }
                // vacancyRepository.vacancyLectures 갱신 → combine → _uiState 자동 반영
            }
        }
    }

    fun confirmDeleteVacancy(lecture: SearchedLecture) {
        viewModelScope.launch {
            _uiState.update { it.copy(dialogState = SearchUiState.DialogState.None) }
            vacancyRepository.removeVacancyLecture(lecture).onFailure { handleSearchError(it); return@launch }
        }
    }

    fun dismissDialog() {
        _uiState.update { it.copy(dialogState = SearchUiState.DialogState.None) }
    }

    fun onTogglePageMode() {
        _uiState.update { it.copy(pageMode = it.pageMode.toggled()) }
    }

    fun onClickBack() {
        if (_uiState.value.bottomSheetType != SearchUiState.BottomSheetType.None) {
            closeBottomSheet()
            return
        }
        _uiState.update { current ->
            if (current.pageMode == PageMode.Bookmark) current.copy(pageMode = PageMode.Search) else current
        }
    }

    fun onToggleLectureContained(lecture: SearchedLecture, contained: Boolean) {
        viewModelScope.launch {
            if (contained) {
                currentTableLectureRepository.removeLecture(lecture).onFailure { handleSearchError(it); return@launch }
                onToggleLectureSelection(lecture)
            } else {
                addLecture(lecture, isForced = false)
            }
        }
    }

    fun confirmAddWithOverlap(lecture: SearchedLecture) {
        viewModelScope.launch {
            _uiState.update { it.copy(dialogState = SearchUiState.DialogState.None) }
            addLecture(lecture, isForced = true)
        }
    }

    fun openFilterSheet() {
        viewModelScope.launch {
            _uiState.update { it.copy(bottomSheetType = SearchUiState.BottomSheetType.Filter) }
            _uiEvent.emit(SearchUiEvent.OpenBottomSheet)
        }
    }

    fun openLectureDetailSheet(lecture: SearchedLecture) {
        viewModelScope.launch {
            val state = _uiState.value
            val referrer = if (state.pageMode == PageMode.Bookmark) {
                DetailScreenReferrer.Bookmark
            } else {
                DetailScreenReferrer.Search(state.searchTitle)
            }
            _uiState.update { it.copy(bottomSheetType = SearchUiState.BottomSheetType.LectureDetail(lecture, referrer)) }
            _uiEvent.emit(SearchUiEvent.OpenBottomSheet)
            fetchBuildings(lecture)
        }
    }

    private suspend fun fetchBuildings(lecture: SearchedLecture) {
        lectureInfoRepository.getBuildings(lecture.lectureSessions.map { it.place }.distinct())
            .onSuccess { buildings ->
                _uiState.update { current ->
                    val bt = current.bottomSheetType
                    if (bt is SearchUiState.BottomSheetType.LectureDetail && bt.lecture.id == lecture.id) {
                        current.copy(bottomSheetType = bt.copy(buildings = buildings))
                    } else current
                }
            }
    }

    fun openDetailReview() {
        _uiState.update { current ->
            val bt = current.bottomSheetType
            if (bt is SearchUiState.BottomSheetType.LectureDetail) {
                current.copy(bottomSheetType = bt.copy(reviewVisible = true))
            } else current
        }
        viewModelScope.launch { _uiEvent.emit(SearchUiEvent.OpenDetailReviewSheet) }
    }

    fun closeDetailReview() {
        _uiState.update { current ->
            val bt = current.bottomSheetType
            if (bt is SearchUiState.BottomSheetType.LectureDetail) {
                current.copy(bottomSheetType = bt.copy(reviewVisible = false))
            } else current
        }
        viewModelScope.launch { _uiEvent.emit(SearchUiEvent.CloseDetailReviewSheet) }
    }

    fun openSyllabus(lecture: SearchedLecture) {
        viewModelScope.launch {
            val courseBook = tableRepository.currentTable.value?.summary?.courseBook ?: return@launch
            lectureInfoRepository.getSyllabusUrl(courseBook, lecture.courseNumber, lecture.lectureNumber)
                .onSuccess { url -> _uiEvent.emit(SearchUiEvent.OpenUrl(url)) }
                .onFailure { handleSearchError(it) }
        }
    }

    fun openReviewSheet(lecture: SearchedLecture) {
        viewModelScope.launch {
            val state = _uiState.value
            val referrer = if (state.pageMode == PageMode.Bookmark) {
                DetailScreenReferrer.Bookmark
            } else {
                DetailScreenReferrer.Search(state.searchTitle)
            }
            _uiState.update { it.copy(bottomSheetType = SearchUiState.BottomSheetType.Review(lecture, referrer)) }
            _uiEvent.emit(SearchUiEvent.OpenBottomSheet)
        }
    }

    fun closeBottomSheet() {
        viewModelScope.launch {
            _uiState.update { it.copy(bottomSheetType = SearchUiState.BottomSheetType.None) }
            _uiEvent.emit(SearchUiEvent.CloseBottomSheet)
        }
    }

    fun applyFilterAndClose() {
        storeRecentSearchedDepartments()
        _uiState.update { it.copy(bottomSheetType = SearchUiState.BottomSheetType.None) }
        viewModelScope.launch { _uiEvent.emit(SearchUiEvent.CloseBottomSheet) }
        onSearch()
    }

    // endregion

    // region Private methods

    private suspend fun query() {
        _querySignal.emit(Unit)
        _uiEvent.emit(SearchUiEvent.ResetScroll)
        val state = _uiState.value
        analyticsLogger.logEvent(
            AnalyticsEvent.SearchLecture(
                SearchLectureParameter(
                    query = state.searchTitle,
                    quarter = tableRepository.currentTable.value?.summary?.courseBook?.semester?.toString() ?: "",
                ),
            ),
        )
    }

    private suspend fun addLecture(lecture: SearchedLecture, isForced: Boolean) {
        currentTableLectureRepository.addLecture(lecture.id, isForced).onFailure { error ->
            if (error is LectureOverlap) {
                _uiState.update {
                    it.copy(
                        dialogState = SearchUiState.DialogState.ConfirmAddWithOverlap(lecture, error.displayMessage),
                    )
                }
            } else {
                handleSearchError(error)
            }
            return
        }
        onToggleLectureSelection(lecture)
    }

    private suspend fun handleSearchError(error: DomainError) {
        val displayMessage = displayMessageResolver.getDisplayMessage(error)
        when (error) {
            is AuthError -> {
                _uiEvent.emit(SearchUiEvent.ShowToast(displayMessage))
                userRepository.postForceLogout()
                _uiEvent.emit(SearchUiEvent.LoggedOut)
            }

            else -> {
                _uiEvent.emit(SearchUiEvent.ShowToast(displayMessage))
            }
        }
    }

    // endregion

    // region Private helpers

    private fun buildSelectableTags(
        allTags: List<SearchTag>,
        selectedTagType: TagType,
        selectedTags: List<SearchTag>,
    ): List<Selectable<SearchTag>> {
        return allTags
            .filter { it.type == selectedTagType }
            .map { it.toDataWithState(selectedTags.contains(it)) }
    }

    private fun buildRecentDepts(
        recentDepts: List<SearchTag>,
        allTags: List<SearchTag>,
        selectedTags: List<SearchTag>,
    ): List<Selectable<SearchTag>> {
        val deptTags = allTags.filter { it.type == TagType.DEPARTMENT }
        return recentDepts
            .filter { recentTag -> deptTags.any { it == recentTag } }
            .map { it.toDataWithState(selectedTags.contains(it)) }
    }

    // endregion
}

data class SearchUiState(
    val pageMode: PageMode,
    val courseBook: CourseBook,

    // 시간표 표시용
    val selectedLecture: SearchedLecture?,
    val currentTableLectures: List<LocalLecture>,
    val tableTrimParam: TableTrimParam,
    val tableLectureCustomOptions: TableLectureCustom,
    val tableTheme: TableTheme,
    val isCompactMode: Boolean,

    // 공통
    val firstBookmarkAlert: Boolean,
    val bookmarks: List<SearchedLecture>,
    val vacancyList: List<SearchedLecture>,
    val disableMapFeature: Boolean,
    val bottomSheetType: BottomSheetType,
    val dialogState: DialogState,

    // Search 전용 (Bookmark 모드에서는 UI에서 무시)
    val searchTitle: String,
    val selectedTags: List<SearchTag>,
    val searchResultListState: SearchResultListState,
    val tagTypes: List<TagType>,
    val selectedTagType: TagType,
    val allSearchTags: List<SearchTag>,       // 전체 태그 (탭 전환 시 재계산용)
    val searchTags: List<Selectable<SearchTag>>,
    val recentSearchedDepartments: List<Selectable<SearchTag>>,
    val draggedTimeBlock: List<List<Boolean>>,
) {
    sealed interface BottomSheetType {
        data object None : BottomSheetType
        data object Filter : BottomSheetType
        data class LectureDetail(
            val lecture: SearchedLecture,
            val referrer: DetailScreenReferrer,
            val buildings: List<LectureBuildingDto> = emptyList(),
            val reviewVisible: Boolean = false,
        ) : BottomSheetType

        data class Review(
            val lecture: SearchedLecture,
            val referrer: DetailScreenReferrer,
        ) : BottomSheetType
    }

    sealed interface DialogState {
        data object None : DialogState
        data class ConfirmDeleteBookmark(val lecture: SearchedLecture) : DialogState
        data class ConfirmDeleteVacancy(val lecture: SearchedLecture) : DialogState
        data class ConfirmAddWithOverlap(val lecture: SearchedLecture, val message: String) : DialogState
    }
}

enum class PageMode {
    Search, Bookmark;

    fun toggled() = if (this == Search) Bookmark else Search
}

sealed interface SearchUiEvent {
    data class ShowToast(val displayMessage: String) : SearchUiEvent
    data object LoggedOut : SearchUiEvent
    data object OpenBottomSheet : SearchUiEvent
    data object CloseBottomSheet : SearchUiEvent
    data class ShowSnackBar(val event: SearchSnackBarEvent) : SearchUiEvent {
        enum class SearchSnackBarEvent {
            FIRST_VACANCY_ADD,
            FIRST_BOOKMARK_ADD,
        }
    }

    data object ResetScroll : SearchUiEvent
    data object OpenDetailReviewSheet : SearchUiEvent
    data object CloseDetailReviewSheet : SearchUiEvent
    data class OpenUrl(val url: String) : SearchUiEvent
}

// Same-package typealias so SearchRouteNew.kt can use SearchSnackBarEvent without full qualification
typealias SearchSnackBarEvent = SearchUiEvent.ShowSnackBar.SearchSnackBarEvent
