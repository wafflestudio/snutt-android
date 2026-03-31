package com.wafflestudio.snutt2.views.logged_in.home.bookmark

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.RemoteConfig
import com.wafflestudio.snutt2.data.bookmark.BookmarkRepository
import com.wafflestudio.snutt2.data.current_table_lecture.CurrentTableLectureRepository
import com.wafflestudio.snutt2.data.lecture_info.LectureInfoRepository
import com.wafflestudio.snutt2.data.notifications.NotificationRepository
import com.wafflestudio.snutt2.data.table_display.TableDisplayRepository
import com.wafflestudio.snutt2.data.tables.TableRepository
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.data.vacancy_noti.VacancyRepository
import com.wafflestudio.snutt2.domain.GetCurrentTableThemeUseCase
import com.wafflestudio.snutt2.domainmodel.SearchedLecture
import com.wafflestudio.snutt2.domainmodel.SyllabusLecture
import com.wafflestudio.snutt2.domainmodel.Table
import com.wafflestudio.snutt2.domainmodel.TableLectureCustom
import com.wafflestudio.snutt2.domainmodel.TableTheme
import com.wafflestudio.snutt2.domainmodel.TableTrimParam
import com.wafflestudio.snutt2.lib.DataWithState
import com.wafflestudio.snutt2.lib.getFittingTrimParam
import com.wafflestudio.snutt2.lib.logging.AddToBookmarkParameter
import com.wafflestudio.snutt2.lib.logging.AddToTimetableParameter
import com.wafflestudio.snutt2.lib.logging.AddToVacancyParameter
import com.wafflestudio.snutt2.lib.logging.AnalyticsEvent
import com.wafflestudio.snutt2.lib.logging.AnalyticsLogger
import com.wafflestudio.snutt2.lib.logging.LectureActionReferrer
import com.wafflestudio.snutt2.lib.network.AuthError
import com.wafflestudio.snutt2.lib.network.DisplayMessageResolver
import com.wafflestudio.snutt2.lib.network.DomainError
import com.wafflestudio.snutt2.lib.network.LectureOverlap
import com.wafflestudio.snutt2.lib.network.dto.core.LectureBuildingDto
import com.wafflestudio.snutt2.lib.network.onFailure
import com.wafflestudio.snutt2.lib.network.onSuccess
import com.wafflestudio.snutt2.lib.toDataWithState
import com.wafflestudio.snutt2.views.logged_in.home.search.LectureState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val currentTableLectureRepository: CurrentTableLectureRepository,
    private val tableRepository: TableRepository,
    private val bookmarkRepository: BookmarkRepository,
    private val tableDisplayRepository: TableDisplayRepository,
    private val userRepository: UserRepository,
    private val vacancyRepository: VacancyRepository,
    private val notificationRepository: NotificationRepository,
    private val lectureInfoRepository: LectureInfoRepository,
    private val getCurrentTableThemeUseCase: GetCurrentTableThemeUseCase,
    private val remoteConfig: RemoteConfig,
    private val displayMessageResolver: DisplayMessageResolver,
    private val analyticsLogger: AnalyticsLogger,
) : ViewModel() {

    private val _uiEvent = MutableSharedFlow<BookmarkUiEvent>(replay = 0)
    val uiEvent = _uiEvent.asSharedFlow()

    private val _uiState: MutableStateFlow<BookmarkUiState> = MutableStateFlow(BookmarkUiState.Loading)
    val uiState: StateFlow<BookmarkUiState> = _uiState.asStateFlow()

    val accessToken: StateFlow<String> get() = userRepository.accessToken

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
                    getCurrentTableThemeUseCase(),
                    notificationRepository.notificationCount,
                    remoteConfig.disableMapFeature,
                    ::Triple,
                ),
                vacancyRepository.vacancyLectures,
                // C-2: courseBook 변경 시 bookmark refetch
                tableRepository.currentTable
                    .filterNotNull()
                    .distinctUntilChanged { o, n -> o.summary.courseBook == n.summary.courseBook }
                    .flatMapLatest { table ->
                        val courseBook = table.summary.courseBook
                        flow {
                            try {
                                bookmarkRepository.fetchBookmarks(courseBook)
                            } catch (_: Exception) {
                                // B-1 API 호출 실패 시 combine 전체가 멈추지 않도록 예외 처리
                            }
                            emitAll(bookmarkRepository.bookmarks.map { it[courseBook] ?: emptyList() })
                        }
                    },
            ) { table, (trimParam, lectureCustom, compact), (theme, notifCount, disableMapFeature), vacancy, bookmarks ->
                _uiState.update { current ->
                    val prev = current as? BookmarkUiState.Success
                    val selectedLecture = prev?.selectedLecture

                    BookmarkUiState.Success(
                        currentTable = table,
                        tableTheme = theme,
                        bookmarkList = bookmarks.map { lecture ->
                            lecture.toDataWithState(
                                LectureState(
                                    selected = lecture.id == selectedLecture?.id,
                                    contained = table.lectures
                                        .filterIsInstance<SyllabusLecture>()
                                        .any { it.originalLectureId == lecture.id },
                                    isBookmarked = true,
                                    isVacancyRegistered = vacancy.any { it.id == lecture.id },
                                ),
                            )
                        },
                        selectedLecture = selectedLecture,
                        tableTrimParam = (table.lectures + listOfNotNull(selectedLecture))
                            .getFittingTrimParam(trimParam),
                        tableLectureCustomOptions = lectureCustom,
                        isCompactMode = compact,
                        uncheckedNotificationCount = notifCount,
                        disableMapFeature = disableMapFeature,
                        vacancyList = vacancy,
                        dialogState = prev?.dialogState ?: BookmarkUiState.DialogState.None,
                        bottomSheetType = when (val bt = prev?.bottomSheetType) {
                            is BookmarkUiState.BottomSheetType.LectureDetail -> bt.copy(
                                isBookmarked = bookmarks.any { it.id == bt.lecture.id },
                                isVacancyRegistered = vacancy.any { it.id == bt.lecture.id },
                            )

                            else -> prev?.bottomSheetType ?: BookmarkUiState.BottomSheetType.None
                        },
                    )
                }
            }.collect()
        }

        // B-2: vacancy 초기 로드
        viewModelScope.launch { vacancyRepository.fetchVacancyLectures() }
    }

    // region Public methods

    fun onToggleLectureSelection(lecture: SearchedLecture) {
        _uiState.update { current ->
            if (current !is BookmarkUiState.Success) return@update current
            val newSelection = if (lecture.id == current.selectedLecture?.id) null else lecture
            current.copy(
                selectedLecture = newSelection,
                bookmarkList = current.bookmarkList.map { item ->
                    item.copy(state = item.state.copy(selected = item.item.id == newSelection?.id))
                },
            )
        }
    }

    fun onClickBookmark(lecture: SearchedLecture) {
        val state = _uiState.value as? BookmarkUiState.Success ?: return
        val isBookmarked = state.bookmarkList.any { it.item.id == lecture.id }

        if (isBookmarked) {
            _uiState.update { state ->
                if (state !is BookmarkUiState.Success) return@update state
                state.copy(dialogState = BookmarkUiState.DialogState.DeleteBookmark(lecture))
            }
        } else {
            viewModelScope.launch {
                analyticsLogger.logEvent(
                    AnalyticsEvent.AddToBookmark(
                        AddToBookmarkParameter(
                            lectureId = lecture.id,
                            referrer = LectureActionReferrer.Bookmark,
                        ),
                    ),
                )
                val courseBook = tableRepository.currentTable.value?.summary?.courseBook ?: return@launch
                bookmarkRepository.addBookmark(courseBook, lecture)
                    .onFailure { handleError(it) }
            }
        }
    }

    fun onConfirmDeleteBookmark(lecture: SearchedLecture) {
        viewModelScope.launch {
            val courseBook = tableRepository.currentTable.value?.summary?.courseBook ?: return@launch
            bookmarkRepository.deleteBookmark(courseBook, lecture)
                .onFailure { handleError(it) }

            _uiState.update { state ->
                if (state !is BookmarkUiState.Success) return@update state
                state.copy(
                    dialogState = BookmarkUiState.DialogState.None,
                    selectedLecture = if (state.selectedLecture?.id == lecture.id) null else state.selectedLecture,
                )
            }
        }
    }

    fun onClickVacancy(lecture: SearchedLecture) {
        val state = _uiState.value as? BookmarkUiState.Success ?: return
        val isVacancyRegistered = state.vacancyList.any { it.id == lecture.id }

        viewModelScope.launch {
            if (isVacancyRegistered) {
                _uiState.update { state ->
                    if (state !is BookmarkUiState.Success) return@update state
                    state.copy(dialogState = BookmarkUiState.DialogState.DeleteVacancyNotification(lecture))
                }
            } else {
                analyticsLogger.logEvent(
                    AnalyticsEvent.AddToVacancy(
                        AddToVacancyParameter(
                            lectureId = lecture.id,
                            referrer = LectureActionReferrer.Bookmark,
                        ),
                    ),
                )
                vacancyRepository.addVacancyLecture(lecture)
                    .onFailure { handleError(it) }
            }
        }
    }

    fun onConfirmDeleteVacancyNotification(lecture: SearchedLecture) {
        viewModelScope.launch {
            _uiState.update { state ->
                if (state !is BookmarkUiState.Success) return@update state
                state.copy(dialogState = BookmarkUiState.DialogState.None)
            }
            vacancyRepository.removeVacancyLecture(lecture)
                .onFailure { handleError(it) }
        }
    }

    fun onToggleLectureContained(lecture: SearchedLecture) {
        val state = _uiState.value as? BookmarkUiState.Success ?: return
        val contained = state.currentTable.lectures
            .filterIsInstance<SyllabusLecture>()
            .any { it.originalLectureId == lecture.id }

        viewModelScope.launch {
            if (contained) {
                currentTableLectureRepository.removeLecture(lecture)
                    .onSuccess { onToggleLectureSelection(lecture) }
                    .onFailure { handleError(it) }
            } else {
                addLecture(lecture, isForced = false)
            }
        }
    }

    fun onConfirmForceAddLecture(lecture: SearchedLecture) {
        viewModelScope.launch {
            addLecture(lecture, isForced = true)

            _uiState.update { state ->
                if (state !is BookmarkUiState.Success) return@update state
                state.copy(dialogState = BookmarkUiState.DialogState.None)
            }
        }
    }

    fun onDismissDialog() {
        _uiState.update { state ->
            if (state !is BookmarkUiState.Success) return@update state
            state.copy(dialogState = BookmarkUiState.DialogState.None)
        }
    }

    fun openLectureDetailSheet(lecture: SearchedLecture) {
        viewModelScope.launch {
            _uiState.update { state ->
                if (state !is BookmarkUiState.Success) return@update state
                state.copy(
                    bottomSheetType = BookmarkUiState.BottomSheetType.LectureDetail(
                        lecture = lecture,
                        isBookmarked = state.bookmarkList.any { it.item.id == lecture.id },
                        isVacancyRegistered = state.vacancyList.any { it.id == lecture.id },
                    ),
                )
            }
            _uiEvent.emit(BookmarkUiEvent.OpenBottomSheet)
            fetchBuildings(lecture)
        }
    }

    fun openReviewSheet(lecture: SearchedLecture) {
        viewModelScope.launch {
            _uiState.update { current ->
                if (current !is BookmarkUiState.Success) return@update current
                current.copy(bottomSheetType = BookmarkUiState.BottomSheetType.Review(lecture))
            }
            _uiEvent.emit(BookmarkUiEvent.OpenBottomSheet)
        }
    }

    fun closeBottomSheet() {
        viewModelScope.launch {
            _uiEvent.emit(BookmarkUiEvent.CloseBottomSheet)
        }
    }

    fun onSheetDismissed() {
        _uiState.update { current ->
            if (current !is BookmarkUiState.Success) return@update current
            current.copy(bottomSheetType = BookmarkUiState.BottomSheetType.None)
        }
    }

    fun onDetailReviewSheetDismissed() {
        _uiState.update { state ->
            if (state !is BookmarkUiState.Success) return@update state
            val bt = state.bottomSheetType
            if (bt is BookmarkUiState.BottomSheetType.LectureDetail) {
                state.copy(bottomSheetType = bt.copy(reviewVisible = false))
            } else {
                state
            }
        }
    }

    fun openDetailReview() {
        _uiState.update { state ->
            if (state !is BookmarkUiState.Success) return@update state
            val bt = state.bottomSheetType
            if (bt is BookmarkUiState.BottomSheetType.LectureDetail) {
                state.copy(bottomSheetType = bt.copy(reviewVisible = true))
            } else state
        }
        viewModelScope.launch { _uiEvent.emit(BookmarkUiEvent.OpenDetailReviewSheet) }
    }

    fun closeDetailReview() {
        _uiState.update { state ->
            if (state !is BookmarkUiState.Success) return@update state
            val bt = state.bottomSheetType
            if (bt is BookmarkUiState.BottomSheetType.LectureDetail) {
                state.copy(bottomSheetType = bt.copy(reviewVisible = false))
            } else state
        }
        viewModelScope.launch { _uiEvent.emit(BookmarkUiEvent.CloseDetailReviewSheet) }
    }

    fun openSyllabus(lecture: SearchedLecture) {
        viewModelScope.launch {
            val courseBook = tableRepository.currentTable.value?.summary?.courseBook ?: return@launch
            lectureInfoRepository.getSyllabusUrl(courseBook, lecture.courseNumber, lecture.lectureNumber)
                .onSuccess { url -> _uiEvent.emit(BookmarkUiEvent.OpenUrl(url)) }
                .onFailure { handleError(it) }
        }
    }

    // endregion

    // region Private methods

    private suspend fun addLecture(lecture: SearchedLecture, isForced: Boolean) {
        analyticsLogger.logEvent(
            AnalyticsEvent.AddToTimetable(
                AddToTimetableParameter(
                    lectureId = lecture.id,
                    timetableId = tableRepository.currentTable.value?.summary?.id,
                    referrer = LectureActionReferrer.Bookmark,
                ),
            ),
        )
        currentTableLectureRepository.addLecture(lecture.id, isForced)
            .onSuccess {
                onToggleLectureSelection(lecture)
            }
            .onFailure { error ->
                if (error is LectureOverlap) {
                    _uiState.update { state ->
                        if (state !is BookmarkUiState.Success) return@update state
                        state.copy(
                            dialogState = BookmarkUiState.DialogState.LectureTimeOverlap(
                                lecture,
                                error.displayMessage,
                            ),
                        )
                    }
                } else {
                    handleError(error)
                }
            }
    }

    private suspend fun fetchBuildings(lecture: SearchedLecture) {
        lectureInfoRepository.getBuildings(lecture.lectureSessions.map { it.place }.distinct())
            .onSuccess { buildings ->
                _uiState.update { current ->
                    if (current !is BookmarkUiState.Success) return@update current
                    val bt = current.bottomSheetType
                    if (bt is BookmarkUiState.BottomSheetType.LectureDetail && bt.lecture.id == lecture.id) {
                        current.copy(bottomSheetType = bt.copy(buildings = buildings))
                    } else current
                }
            }
    }

    private suspend fun handleError(error: DomainError) {
        val displayMessage = displayMessageResolver.getDisplayMessage(error)
        when (error) {
            is AuthError -> {
                _uiEvent.emit(BookmarkUiEvent.ShowToast(displayMessage))
                userRepository.postForceLogout()
                _uiEvent.emit(BookmarkUiEvent.NavigateToOnboard)
            }

            else -> {
                _uiEvent.emit(BookmarkUiEvent.ShowToast(displayMessage))
            }
        }
    }

    // endregion
}

sealed interface BookmarkUiEvent {
    data class ShowToast(val message: String) : BookmarkUiEvent
    data object NavigateToOnboard : BookmarkUiEvent
    data object OpenBottomSheet : BookmarkUiEvent
    data object CloseBottomSheet : BookmarkUiEvent
    data object OpenDetailReviewSheet : BookmarkUiEvent
    data object CloseDetailReviewSheet : BookmarkUiEvent
    data class OpenUrl(val url: String) : BookmarkUiEvent
}

sealed interface BookmarkUiState {
    /** 현재 열려 있는 바텀시트 타입. 열린 시트가 없으면 null. */
    val activeBottomSheet: BottomSheetType?
        get() = (this as? Success)?.bottomSheetType
            ?.takeIf { it != BottomSheetType.None }

    data object Loading : BookmarkUiState

    data class Success(
        val currentTable: Table,
        val tableTheme: TableTheme,
        val bookmarkList: List<DataWithState<SearchedLecture, LectureState>>,
        val selectedLecture: SearchedLecture?,
        val tableTrimParam: TableTrimParam,
        val tableLectureCustomOptions: TableLectureCustom,
        val isCompactMode: Boolean,
        val uncheckedNotificationCount: Long,
        val disableMapFeature: Boolean,
        val vacancyList: List<SearchedLecture>,
        val dialogState: DialogState = DialogState.None,
        val bottomSheetType: BottomSheetType = BottomSheetType.None,
    ) : BookmarkUiState

    sealed interface DialogState {
        data object None : DialogState
        data class DeleteBookmark(val lecture: SearchedLecture) : DialogState
        data class DeleteVacancyNotification(val lecture: SearchedLecture) : DialogState
        data class LectureTimeOverlap(val lecture: SearchedLecture, val displayMessage: String) : DialogState
    }

    sealed interface BottomSheetType {
        data object None : BottomSheetType
        data class LectureDetail(
            val lecture: SearchedLecture,
            val buildings: List<LectureBuildingDto> = emptyList(),
            val reviewVisible: Boolean = false,
            val isBookmarked: Boolean = false,
            val isVacancyRegistered: Boolean = false,
        ) : BottomSheetType

        data class Review(val lecture: SearchedLecture) : BottomSheetType
    }
}
