package com.wafflestudio.snutt2.views.logged_in.home.search

import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.map
import com.wafflestudio.snutt2.data.current_table.CurrentTableRepository
import com.wafflestudio.snutt2.data.lecture_search.LectureSearchRepository
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.data.vacancy_noti.VacancyRepository
import com.wafflestudio.snutt2.domainmodel.TableTrimParam
import com.wafflestudio.snutt2.lib.Selectable
import com.wafflestudio.snutt2.lib.concatenate
import com.wafflestudio.snutt2.lib.flatMapToSearchTimeDto
import com.wafflestudio.snutt2.lib.isLectureNumberEquals
import com.wafflestudio.snutt2.lib.logging.AddToBookmarkParameter
import com.wafflestudio.snutt2.lib.logging.AddToVacancyParameter
import com.wafflestudio.snutt2.lib.logging.AnalyticsEvent
import com.wafflestudio.snutt2.lib.logging.AnalyticsLogger
import com.wafflestudio.snutt2.lib.logging.LectureActionReferrer
import com.wafflestudio.snutt2.lib.logging.SearchLectureParameter
import com.wafflestudio.snutt2.lib.network.ApiOnError
import com.wafflestudio.snutt2.lib.network.AuthError
import com.wafflestudio.snutt2.lib.network.DisplayMessageResolver
import com.wafflestudio.snutt2.lib.network.DomainError
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.lib.network.toDomainError
import com.wafflestudio.snutt2.lib.toDataWithState
import com.wafflestudio.snutt2.model.SearchTimeDto
import com.wafflestudio.snutt2.model.TagDto
import com.wafflestudio.snutt2.model.TagType
import com.wafflestudio.snutt2.views.logged_in.home.search.bookmark.SearchPageMode
import com.wafflestudio.snutt2.views.logged_in.home.search.search_option.clusterToTimeBlocks
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val currentTableRepository: CurrentTableRepository,
    private val lectureSearchRepository: LectureSearchRepository,
    private val vacancyRepository: VacancyRepository,
    private val userRepository: UserRepository,
    private val apiOnError: ApiOnError,
    private val displayMessageResolver: DisplayMessageResolver,
    private val analyticsLogger: AnalyticsLogger,
) : ViewModel() {

    private val _searchUiEvent: MutableSharedFlow<SearchUiEvent> = MutableSharedFlow(replay = 0)
    val searchUiEvent = _searchUiEvent

    private val _searchResultListState = MutableStateFlow(SearchResultListState.PLACEHOLDER)
    val searchResultListState = _searchResultListState

    var lazyListState = LazyListState(0, 0)

    private val _searchTitle = MutableStateFlow("")
    val searchTitle = _searchTitle.asStateFlow()

    private val _selectedLecture = MutableStateFlow<LectureDto?>(null)
    val selectedLecture = _selectedLecture.asStateFlow()

    private val _selectedTagType = MutableStateFlow<TagType>(TagType.SORT_CRITERIA)
    val selectedTagType = _selectedTagType.asStateFlow()

    private val _selectedTags = MutableStateFlow(listOf<TagDto>())
    val selectedTags = _selectedTags.asStateFlow()

    private val _searchTagListFlow = MutableStateFlow(listOf<TagDto>())

    private val currentTable = currentTableRepository.currentTable

    private val vacancyList = MutableStateFlow(listOf<LectureDto>())

    val firstBookmarkAlert = lectureSearchRepository.firstBookmarkAlert

    val firstVacancyAdd = vacancyRepository.firstVacancyAdd

    val semesterChange =
        currentTable
            .filterNotNull()
            .map { it.year * 10 + it.semester } // .distinctUntilChanged()

    private val _querySignal = MutableSharedFlow<Unit>(replay = 0)
    private val _getBookmarkListSignal = MutableSharedFlow<Unit>(replay = 0)

    private val _pageMode = MutableStateFlow(SearchPageMode.Search)
    val pageMode: StateFlow<SearchPageMode> get() = _pageMode

    // 드래그하고 확정한 시간대 검색 격자 (월~금 5칸, 8시~22시 30분 간격 28칸)
    private val _draggedTimeBlock =
        MutableStateFlow(TableTrimParam.TimeBlockGridDefault)
    val draggedTimeBlock = _draggedTimeBlock.asStateFlow()

    // 검색 쿼리에 들어가는 시간대 검색 시간대 목록
    private val _searchTimeList = MutableStateFlow<List<SearchTimeDto>?>(null)

    val recentSearchedDepartments: StateFlow<List<TagDto>> = lectureSearchRepository.recentSearchedDepartments

    private val timeTags = listOf(TagDto.TIME_EMPTY, TagDto.TIME_SELECT)
    private val etcTags = listOf(TagDto.ETC_ENG, TagDto.ETC_MILITARY)

    // 2025년 이전 학기에는 "구) 교양분류" 타입의 태그가 내려오지 않는다.
    val tagTypesNotEmpty: StateFlow<List<TagType>> = _searchTagListFlow
        .map { tags ->
            (tags + etcTags + timeTags).map { it.type }.toSet().toList()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    init {
        viewModelScope.launch {
            semesterChange.distinctUntilChanged().collectLatest {
                clear()
                try {
                    fetchSearchTagList()
                    getBookmarkList()
                } catch (e: Exception) {
                    apiOnError(e)
                }
            }
        }
        viewModelScope.launch {
            // 25년 이후에서 구) 교양분류를 선택한 상태로 25년 이전으로 이동하면, 태그 선택지가 사라져서, 빈 sheet가 뜨게 된다. 이를 방지.
            combine(tagTypesNotEmpty, _selectedTagType) { tagTypes, selectedTagType ->
                !tagTypes.contains(selectedTagType)
            }.distinctUntilChanged().filter { it }.collectLatest {
                setTagType(TagType.SORT_CRITERIA)
            }
        }
        viewModelScope.launch {
            vacancyList.emit(
                vacancyRepository.getVacancyLectures()
                    .sortedByDescending { it.wasFull && it.registrationCount < it.quota },
            )
        }
    }

    val tagsByTagType: StateFlow<List<Selectable<TagDto>>> = combine(
        _searchTagListFlow, _selectedTagType, _selectedTags,
    ) { tags, selectedTagType, selectedTags ->
        (tags + etcTags + timeTags).filter { it.type == selectedTagType }
            .map { it.toDataWithState(selectedTags.contains(it)) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val selectableRecentSearchedDepartments: StateFlow<List<Selectable<TagDto>>> = combine(
        tagsByTagType, recentSearchedDepartments, _selectedTags,
    ) { tagsByTagType, recentDepartments, selectedTags ->
        val tagItemsByTagType = tagsByTagType.map { tag -> tag.item }
        recentDepartments.filter { recentDepartment ->
            tagItemsByTagType.contains(recentDepartment)
        }.map { it.toDataWithState(selectedTags.contains(it)) }
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        emptyList(),
    )

    val bookmarkList = combine(
        _getBookmarkListSignal.flatMapLatest {
            try {
                flowOf(currentTableRepository.getBookmarks())
            } catch (e: Exception) {
                flowOf(emptyList())
            }
        },
        _selectedLecture,
        currentTable.filterNotNull(),
        vacancyList,
    ) { bookmarks, selectedLecture, currentTable, vacancyList ->
        bookmarks.map { bookmarkedLecture ->
            bookmarkedLecture.toDataWithState(
                LectureState(
                    selected = selectedLecture == bookmarkedLecture,
                    contained = currentTable.lectureList.any { lectureOfCurrentTable ->
                        lectureOfCurrentTable.isLectureNumberEquals(bookmarkedLecture)
                    },
                    isBookmarked = true,
                    isVacancyRegistered = vacancyList.any { vacancyRegisteredLecture ->
                        vacancyRegisteredLecture.isLectureNumberEquals(bookmarkedLecture)
                    },
                ),
            )
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        emptyList(),
    )

    val queryResults = combine(
        _querySignal.flatMapLatest {
            val currentTable = currentTable.filterNotNull().first()
            lectureSearchRepository.getLectureSearchResultStream(
                year = currentTable.year,
                semester = currentTable.semester,
                title = _searchTitle.value,
                tags = _selectedTags.value,
                times = _searchTimeList.value,
                timesToExclude = if (_selectedTags.value.contains(TagDto.TIME_EMPTY)) currentTable.lectureList.flatMapToSearchTimeDto() else null,
                scope = viewModelScope,
            )
        },
        _selectedLecture,
        currentTable.filterNotNull(),
        _getBookmarkListSignal.flatMapLatest {
            try {
                flowOf(currentTableRepository.getBookmarks())
            } catch (e: Exception) {
                flowOf(emptyList())
            }
        },
        vacancyList,
    ) { pagingDataResult, selectedLecture, currentTable, bookmarks, vacancyList ->
        pagingDataResult.map { searchedLecture ->
            searchedLecture.toDataWithState(
                LectureState(
                    selected = selectedLecture == searchedLecture,
                    contained = currentTable.lectureList.any { lectureOfCurrentTable ->
                        lectureOfCurrentTable.isLectureNumberEquals(searchedLecture)
                    },
                    isBookmarked = bookmarks.any { bookmarkedLecture ->
                        bookmarkedLecture.isLectureNumberEquals(searchedLecture)
                    },
                    isVacancyRegistered = vacancyList.any { vacancyRegisteredLecture ->
                        vacancyRegisteredLecture.isLectureNumberEquals(searchedLecture)
                    },
                ),
            )
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        PagingData.empty(),
    )

    suspend fun setTitle(title: String) {
        _searchTitle.emit(title)
    }

    fun setTagType(tagType: TagType) {
        viewModelScope.launch {
            _selectedTagType.emit(tagType)
        }
    }

    fun onToggleLectureSelection(lecture: LectureDto) {
        viewModelScope.launch {
            toggleLectureSelection(lecture)
        }
    }

    suspend fun toggleLectureSelection(lecture: LectureDto) {
        if (lecture == _selectedLecture.value) {
            _selectedLecture.emit(null)
        } else {
            _selectedLecture.emit(lecture)
        }
    }

    fun onToggleTag(tag: TagDto) {
        viewModelScope.launch {
            if (_selectedTags.value.contains(tag)) {
                _selectedTags.emit(_selectedTags.value.filter { it != tag })

                if (tag == TagDto.TIME_SELECT) {
                    _searchTimeList.emit(null)
                }
            } else {
                val selectedTags = if (tag.type.isExclusive) {
                    concatenate(_selectedTags.value.filter { it.type != tag.type }, listOf(tag))
                } else {
                    concatenate(_selectedTags.value, listOf(tag))
                }
                _selectedTags.emit(selectedTags)

                if (tag == TagDto.TIME_SELECT) {
                    _draggedTimeBlock.value.clusterToTimeBlocks().let {
                        if (it.isEmpty()) {
                            _searchTimeList.emit(null)
                        } else {
                            _searchTimeList.emit(it)
                        }
                    }
                }
            }
        }
    }

    fun onLoadStateChanged(loadState: CombinedLoadStates) { // FIXME: PagingLoadState -> UI -> ViewModel로 단방향 흐름에 위배됨, LoadState를 ViewModel에서 볼 수 있게 수정할 것.
        val refreshState = loadState.refresh
        if (refreshState is LoadState.Error) {
            val error = (refreshState.error as? Exception)?.toDomainError()
            if (error == null) return
            viewModelScope.launch {
                _searchResultListState.emit(SearchResultListState.EMPTY) // FIXME: loadState가 에러여도 PagingData는 그대로 남아있고, 그냥 EMPTY로 보이지 않게 해둠
                handleSearchError(error)
            }
        }
    }

    suspend fun query() {
        _querySignal.emit(Unit)
        lazyListState = LazyListState(0, 0)
        analyticsLogger.logEvent(
            AnalyticsEvent.SearchLecture(
                SearchLectureParameter(
                    query = searchTitle.value,
                    quarter = currentTable.value?.semester?.toString() ?: "",
                ),
            ),
        )
    }

    fun onSearch() {
        viewModelScope.launch {
            _searchResultListState.emit(SearchResultListState.HAS_RESULTS)
            query()
        }
    }

    suspend fun getBookmarkList() {
        _getBookmarkListSignal.emit(Unit)
    }

    fun onClearEditText() {
        viewModelScope.launch {
            _searchTitle.emit("")
        }
    }

    fun onClickBookmark(lecture: LectureDto, isBookmarked: Boolean) {
        viewModelScope.launch {
            if (isBookmarked) {
                _searchUiEvent.emit(
                    SearchUiEvent.ShowAlertByEvent(
                        event = SearchAlertEvent.DELETE_BOOKMARK,
                        onConfirm = {
                            deleteBookmark(lecture)
                            toggleLectureSelection(lecture)
                        },
                    ),
                )
            } else {
                analyticsLogger.logEvent(
                    AnalyticsEvent.AddToBookmark(
                        AddToBookmarkParameter(
                            lectureId = lecture.lecture_id ?: lecture.id,
                            referrer = LectureActionReferrer.Search(searchTitle.value),
                        ),
                    ),
                )
                addBookmark(lecture)
                if (firstBookmarkAlert.value) {
                    setFirstBookmarkAlertShown()
                    _searchUiEvent.emit(SearchUiEvent.ShowSnackBarByEvent(SearchSnackBarEvent.FIRST_BOOKMARK_ADD))
                }
            }
        }
    }

    suspend fun addBookmark(lecture: LectureDto) {
        currentTableRepository.addBookmark(lecture)
        getBookmarkList()
    }

    suspend fun deleteBookmark(lecture: LectureDto) {
        currentTableRepository.deleteBookmark(lecture)
        getBookmarkList()
    }

    suspend fun getBookmarkLecture(year: Long, semester: Long, lectureId: String): LectureDto? {
        return currentTableRepository.getBookmarksOfSemester(year, semester).find {
            it.id == lectureId
        }
    }

    fun onClickVacancy(lecture: LectureDto, isVacancyRegistered: Boolean) {
        viewModelScope.launch {
            if (isVacancyRegistered) {
                removeVacancyLecture(lecture)
            } else {
                analyticsLogger.logEvent(
                    AnalyticsEvent.AddToVacancy(
                        AddToVacancyParameter(
                            lectureId = lecture.lecture_id ?: lecture.id,
                            referrer = LectureActionReferrer.Search(searchTitle.value),
                        ),
                    ),
                )
                addVacancyLecture(lecture)
            }
        }
    }
    private suspend fun getVacancyLectures() {
        vacancyList.emit(
            vacancyRepository.getVacancyLectures()
                .sortedByDescending { it.wasFull && it.registrationCount < it.quota },
        )
    }

    suspend fun addVacancyLecture(lecture: LectureDto) {
        vacancyRepository.addVacancyLecture(lecture.id)
        if (firstVacancyAdd.value) {
            vacancyRepository.setVacancyAdded()
            _searchUiEvent.emit(SearchUiEvent.ShowSnackBarByEvent(SearchSnackBarEvent.FIRST_VACANCY_ADD))
        }
        getVacancyLectures()
    }

    suspend fun removeVacancyLecture(lecture: LectureDto) {
        _searchUiEvent.emit(
            SearchUiEvent.ShowAlertByEvent(
                event = SearchAlertEvent.DELETE_VACANCY,
                onConfirm = {
                    vacancyRepository.removeVacancyLecture(lecture.id)
                    getVacancyLectures()
                    toggleLectureSelection(lecture)
                },
            ),
        )
    }

    fun onTimeSelectCancel() {
        viewModelScope.launch {
            if (_draggedTimeBlock.value.all { it.all { it.not() } }) {
                onToggleTag(TagDto.TIME_SELECT)
            }
        }
    }

    fun onTimeSelectConfirm(draggedTimeBlock: List<List<Boolean>>) {
        viewModelScope.launch {
            _draggedTimeBlock.emit(draggedTimeBlock)
            draggedTimeBlock.clusterToTimeBlocks().let {
                if (it.isEmpty()) {
                    _searchTimeList.emit(null)
                } else {
                    _searchTimeList.emit(it)
                }
            }

            // 시간대를 하나도 선택을 안 하고 완료를 누르면 태그 선택도 해제하기 (다시 누를 수 있게)
            if (draggedTimeBlock.all { it.all { it.not() } }) {
                onToggleTag(TagDto.TIME_SELECT)
            }
        }
    }

    fun storeRecentSearchedDepartments() {
        _selectedTags.value.filter { it.type == TagType.DEPARTMENT }.forEach { tag ->
            lectureSearchRepository.storeRecentSearchedDepartment(tag)
        }
    }

    fun removeRecentSearchedDepartment(tag: TagDto) {
        lectureSearchRepository.removeRecentSearchedDepartment(tag)
    }

    fun setFirstBookmarkAlertShown() {
        lectureSearchRepository.setFirstBookmarkAlertShown()
    }

    private suspend fun clear() {
        _searchTitle.emit("")
        _selectedLecture.emit(null)
        _searchTagListFlow.emit(emptyList())
        _selectedTags.emit(emptyList())
        lazyListState = LazyListState(0, 0)
        /* TODO
         * 기존 구현은 보고 있는 학기가 바뀔 때 query signal 을 줘서 검색어 "" 로 재검색을 하게 한다.
         * 하지만 검색을 안 한 상태(loadState 가 NotLoading 인 상태)로 되돌리는 게 더 낫지 않을까?
         */
    }

    private suspend fun fetchSearchTagList() {
        val currentTable = currentTable.filterNotNull().first()
        _searchTagListFlow.emit(
            lectureSearchRepository.getSearchTags(
                currentTable.year, currentTable.semester,
            ),
        )
    }

    fun onTogglePageMode() {
        viewModelScope.launch {
            _pageMode.emit(_pageMode.value.toggled())
        }
    }

    fun onClickBack() {
        viewModelScope.launch {
            if (_pageMode.value == SearchPageMode.Bookmark) {
                _pageMode.emit(SearchPageMode.Search)
            }
        }
    }

    private suspend fun handleSearchError(error: DomainError) {
        val displayMessage = displayMessageResolver.getDisplayMessage(error)
        when (error) {
            is AuthError -> {
                _searchUiEvent.emit(SearchUiEvent.ShowToastError(displayMessage))
                userRepository.postForceLogout()
                _searchUiEvent.emit(SearchUiEvent.LoggedOut)
            }
            else -> {
                _searchUiEvent.emit(SearchUiEvent.ShowToastError(displayMessage))
            }
        }
    }
}

sealed interface SearchUiEvent {
    data class ShowToastError(val displayMessage: String) : SearchUiEvent
    data class ShowToast(val resId: Int) : SearchUiEvent
    data object LoggedOut : SearchUiEvent
    data class ShowAlertByEvent(val event: SearchAlertEvent, val onConfirm: suspend () -> Unit) : SearchUiEvent
    data class ShowSnackBarByEvent(val event: SearchSnackBarEvent) : SearchUiEvent
//    data object ShowBottomSheet: SearchUiEvent
}

enum class SearchSnackBarEvent {
    FIRST_VACANCY_ADD,
    FIRST_BOOKMARK_ADD,
}

enum class SearchAlertEvent {
    DELETE_BOOKMARK,
    DELETE_VACANCY,
}
