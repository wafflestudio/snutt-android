package com.wafflestudio.snutt2.feature.home.timetable

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.config.RemoteConfig
import com.wafflestudio.snutt2.data.course_books.CourseBookRepository
import com.wafflestudio.snutt2.data.notifications.NotificationRepository
import com.wafflestudio.snutt2.data.table_display.TableDisplayRepository
import com.wafflestudio.snutt2.data.tables.TableRepository
import com.wafflestudio.snutt2.domain.GetCurrentTableThemeUseCase
import com.wafflestudio.snutt2.domain.model.Table
import com.wafflestudio.snutt2.domain.model.TableLectureCustom
import com.wafflestudio.snutt2.domain.model.TableSummary
import com.wafflestudio.snutt2.domain.model.TableTheme
import com.wafflestudio.snutt2.domain.model.TableTrimParam
import com.wafflestudio.snutt2.lib.getFittingTrimParam
import com.wafflestudio.snutt2.domain.DisplayMessageResolver
import com.wafflestudio.snutt2.domain.DomainError
import com.wafflestudio.snutt2.data.onFailure
import com.wafflestudio.snutt2.data.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimeTableViewModel @Inject constructor(
    private val tableDisplayRepository: TableDisplayRepository,
    private val getCurrentTableThemeUseCase: GetCurrentTableThemeUseCase,
    private val tableRepository: TableRepository,
    private val notificationRepository: NotificationRepository,
    private val courseBookRepository: CourseBookRepository,
    private val remoteConfig: RemoteConfig,
    private val displayMessageResolver: DisplayMessageResolver,
) : ViewModel() {

    private val _uiState = MutableStateFlow<TimeTableUiState>(TimeTableUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<TimeTableUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    // FIXME: TimeTableViewModelNew 은 Home 의 NavBackStackEntry의 ViewModelStoreOwner에 스코프되는데,
    // Home 은 시작점이라 앱 끌 때까지 사라지지 않음. 그래서 refresh 를 composable 에서 LaunchedEffect 로 불러줘야 함.
    // 이 구조가 최선인지 고민해보기
    fun refresh() {
        viewModelScope.launch {
            notificationRepository.fetchNotificationCount()
        }
        viewModelScope.launch {
            courseBookRepository.fetchCourseBooks()
        }
    }

    init {
        viewModelScope.launch {
            combine(
                tableRepository.currentTable.filterNotNull(),
                getCurrentTableThemeUseCase(),
                combine(
                    tableDisplayRepository.tableTrimParam,
                    tableDisplayRepository.compactMode,
                    tableDisplayRepository.tableLectureCustomOption,
                    ::Triple,
                ),
                combine(
                    tableRepository.tableSummaryList,
                    courseBookRepository.courseBooks,
                    ::Pair,
                ),
                combine(
                    notificationRepository.notificationCount,
                    remoteConfig.vacancyNotificationBannerEnabled,
                    tableDisplayRepository.isVisitedSessionlessLectureList,
                    ::Triple,
                ),
            ) { table, theme, (tableTrimParam, compactMode, tableLectureCustomOption), (tableMap, coursebooks), (count, vacancy, isVisited) ->
                val prevState = _uiState.value as? TimeTableUiState.Loaded
                _uiState.update {
                    TimeTableUiState.Loaded(
                        table = table,
                        theme = theme,
                        previewTheme = prevState?.previewTheme,
                        tableTrimParam = if (tableTrimParam.forceFitLectures) {
                            table.lectures.getFittingTrimParam(TableTrimParam.Default)
                        } else {
                            tableTrimParam
                        },
                        isCompactMode = compactMode,
                        tableLectureCustomOptions = tableLectureCustomOption,
                        newSemesterExist = coursebooks.let { coursebooks ->
                            val mostRecentCourseBook = coursebooks.firstOrNull() ?: return@let false

                            tableMap.none { table ->
                                table.courseBookEquals(mostRecentCourseBook)
                            }
                        },
                        uncheckedNotificationExist = count > 0,
                        vacancyNotificationBannerEnabled = vacancy,
                        isSessionlessLectureHintVisible = !isVisited && table.lectures.any { it.lectureSessions.isEmpty() },
                        dialogState = prevState?.dialogState ?: TimeTableUiState.DialogState.None,
                    )
                }
            }.collect()
        }
    }

    fun visitSessionlessLectureList() {
        _uiState.update { state ->
            when (state) {
                is TimeTableUiState.Loaded -> state.copy(isSessionlessLectureHintVisible = false)
                else -> state
            }
        }
        viewModelScope.launch {
            tableDisplayRepository.visitSessionlessLectureList()
        }
    }

    fun setPreviewTheme(tableTheme: TableTheme) {
        _uiState.update { state ->
            when (state) {
                is TimeTableUiState.Loaded -> state.copy(previewTheme = tableTheme)
                else -> state
            }
        }
    }

    fun resetPreviewTheme() {
        _uiState.update { state ->
            when (state) {
                is TimeTableUiState.Loaded -> state.copy(previewTheme = null)
                else -> state
            }
        }
    }

    fun showTableTitleChangeDialog(tableSummary: TableSummary) {
        _uiState.update { state ->
            when (state) {
                is TimeTableUiState.Loaded -> state.copy(dialogState = TimeTableUiState.DialogState.ChangeTableName(tableSummary))
                else -> state
            }
        }
    }

    fun changeTableTitle(table: TableSummary, newTitle: String) {
        viewModelScope.launch {
            tableRepository.updateTableName(table, newTitle)
                .onFailure {
                    handleError(it)
                }.onSuccess {
                    _uiState.update { state ->
                        when (state) {
                            is TimeTableUiState.Loaded -> state.copy(
                                dialogState = TimeTableUiState.DialogState.None,
                            )

                            else -> state
                        }
                    }
                }
        }
    }

    fun dismissDialog() {
        _uiState.update { state ->
            when (state) {
                is TimeTableUiState.Loaded -> state.copy(dialogState = TimeTableUiState.DialogState.None)
                else -> state
            }
        }
    }

    private fun handleError(error: DomainError) { // TODO: 네트워크 오류일 때 재시도하기(지금은 앱 껐다 켜야 됨)
        val displayMessage = displayMessageResolver.getDisplayMessage(error)
        viewModelScope.launch {
            _uiEvent.emit(TimeTableUiEvent.ShowToast(displayMessage))
        }
    }
}

sealed interface TimeTableUiState {
    data object Loading : TimeTableUiState
    data class Loaded(
        val table: Table,
        val theme: TableTheme,
        val previewTheme: TableTheme?,
        val tableTrimParam: TableTrimParam,
        val isCompactMode: Boolean,
        val tableLectureCustomOptions: TableLectureCustom,
        val newSemesterExist: Boolean,
        val uncheckedNotificationExist: Boolean,
        val vacancyNotificationBannerEnabled: Boolean,
        val isSessionlessLectureHintVisible: Boolean,
        val dialogState: DialogState,
    ) : TimeTableUiState

    sealed interface DialogState {
        data object None : DialogState
        data class ChangeTableName(
            val tableSummary: TableSummary,
        ) : DialogState
    }
}

sealed interface TimeTableUiEvent {
    data class ShowToast(val displayMessage: String) : TimeTableUiEvent
}
