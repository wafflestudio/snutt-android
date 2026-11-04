package com.wafflestudio.snutt2.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.data.Result
import com.wafflestudio.snutt2.data.semesterstatus.SemesterStatusRepository
import com.wafflestudio.snutt2.data.tables.TableRepository
import com.wafflestudio.snutt2.domain.DisplayMessageResolver
import com.wafflestudio.snutt2.domain.model.LectureReminderOffset
import com.wafflestudio.snutt2.domain.model.LectureWithReminderOption
import com.wafflestudio.snutt2.domain.model.SemesterStatus
import com.wafflestudio.snutt2.domain.model.TableSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class LectureReminderViewModel @Inject constructor(
    private val tableRepository: TableRepository,
    private val semesterStatusRepository: SemesterStatusRepository,
    private val displayMessageResolver: DisplayMessageResolver,
) : ViewModel() {
    private val _uiState = MutableStateFlow<LectureReminderUiState>(LectureReminderUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<LectureReminderUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private var loadJob: Job? = null
    private var contentVersion = 0L

    init {
        loadReminders()
    }

    fun retry() {
        loadReminders()
    }

    fun onSelectOffset(lectureId: String, offset: LectureReminderOffset) {
        val current = _uiState.value as? LectureReminderUiState.Loaded ?: return
        val lecture = current.lectures.find { it.lectureId == lectureId } ?: return
        if (lectureId in current.savingLectureIds || lecture.lectureReminderOffset == offset) return

        val requestVersion = contentVersion
        _uiState.update { current.copy(savingLectureIds = current.savingLectureIds + lectureId) }
        viewModelScope.launch {
            val result = tableRepository.updateTimetableLectureReminder(
                timetableId = current.timetableId,
                lectureId = lectureId,
                offset = offset,
            )
            // 조회나 대표시간표 변경 후 도착한 이전 저장 결과는 화면에 반영하지 않는다.
            if (contentVersion != requestVersion) return@launch

            when (result) {
                is Result.Success -> {
                    _uiState.update {
                        if (it is LectureReminderUiState.Loaded && it.timetableId == current.timetableId) {
                            it.copy(
                                savingLectureIds = it.savingLectureIds - lectureId,
                                lectures = it.lectures.map { item ->
                                    if (item.lectureId == lectureId) {
                                        item.copy(lectureReminderOffset = result.data.lectureReminderOffset)
                                    } else {
                                        item
                                    }
                                },
                            )
                        } else {
                            it
                        }
                    }
                    _uiEvent.emit(LectureReminderUiEvent.ReminderUpdated(result.data.lectureReminderOffset))
                }
                is Result.Fail -> {
                    _uiState.update {
                        if (it is LectureReminderUiState.Loaded && it.timetableId == current.timetableId) {
                            it.copy(savingLectureIds = it.savingLectureIds - lectureId)
                        } else {
                            it
                        }
                    }
                    _uiEvent.emit(
                        LectureReminderUiEvent.ShowToast(displayMessageResolver.getDisplayMessage(result.error)),
                    )
                }
            }
        }
    }

    private fun loadReminders() {
        loadJob?.cancel()
        contentVersion++
        _uiState.update { LectureReminderUiState.Loading }
        loadJob = viewModelScope.launch {
            val tables = flow<Result<List<TableSummary>>> {
                when (val result = tableRepository.fetchTableList()) {
                    is Result.Success -> emitAll(tableRepository.tableSummaryList.map { Result.Success(it) })
                    is Result.Fail -> emit(result)
                }
            }
            val semesters = flow<Result<SemesterStatus>> {
                when (val result = semesterStatusRepository.fetchSemesterStatus()) {
                    is Result.Success -> emitAll(
                        semesterStatusRepository.semesterStatus.filterNotNull().map { Result.Success(it) },
                    )
                    is Result.Fail -> emit(result)
                }
            }
            combine(tables, semesters) { tableResult, semesterResult ->
                when {
                    tableResult is Result.Fail -> tableResult
                    semesterResult is Result.Fail -> semesterResult
                    tableResult is Result.Success && semesterResult is Result.Success -> Result.Success(
                        tableResult.data.firstOrNull {
                            it.isPrimary && semesterResult.data.isActiveSemester(it.courseBook)
                        }?.id,
                    )
                    else -> error("Unexpected result")
                }
            }
                .distinctUntilChanged()
                .flatMapLatest { primaryTableResult ->
                    flow<LectureReminderUiState> {
                        when (primaryTableResult) {
                            is Result.Fail -> emit(
                                LectureReminderUiState.Error(
                                    displayMessageResolver.getDisplayMessage(primaryTableResult.error),
                                ),
                            )
                            is Result.Success -> {
                                val tableId = primaryTableResult.data
                                if (tableId == null) {
                                    emit(LectureReminderUiState.Empty)
                                } else {
                                    emit(LectureReminderUiState.Loading)
                                    when (val result = tableRepository.getTimetableReminders(tableId)) {
                                        is Result.Success -> emit(
                                            if (result.data.lectureReminders.isEmpty()) {
                                                LectureReminderUiState.Empty
                                            } else {
                                                LectureReminderUiState.Loaded(
                                                    timetableId = result.data.timetableId,
                                                    lectures = result.data.lectureReminders,
                                                )
                                            },
                                        )
                                        is Result.Fail -> emit(
                                            LectureReminderUiState.Error(
                                                displayMessageResolver.getDisplayMessage(result.error),
                                            ),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                .collect { state ->
                    contentVersion++
                    _uiState.update { state }
                }
        }
    }
}

sealed interface LectureReminderUiState {
    data object Loading : LectureReminderUiState
    data object Empty : LectureReminderUiState
    data class Loaded(
        val timetableId: String,
        val lectures: List<LectureWithReminderOption>,
        val savingLectureIds: Set<String> = emptySet(),
    ) : LectureReminderUiState
    data class Error(val message: String) : LectureReminderUiState
}

sealed interface LectureReminderUiEvent {
    data class ShowToast(val message: String) : LectureReminderUiEvent
    data class ReminderUpdated(val offset: LectureReminderOffset) : LectureReminderUiEvent
}
