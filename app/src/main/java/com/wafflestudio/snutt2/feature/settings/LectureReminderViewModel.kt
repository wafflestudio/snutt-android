package com.wafflestudio.snutt2.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.data.onFailure
import com.wafflestudio.snutt2.data.onSuccess
import com.wafflestudio.snutt2.data.semester_status.SemesterStatusRepository
import com.wafflestudio.snutt2.data.tables.TableRepository
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.domain.AuthError
import com.wafflestudio.snutt2.domain.DisplayMessageResolver
import com.wafflestudio.snutt2.domain.DomainError
import com.wafflestudio.snutt2.domain.model.LectureReminderOffset
import com.wafflestudio.snutt2.domain.model.LectureWithReminderOption
import com.wafflestudio.snutt2.lib.debouncePerKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LectureReminderViewModel @Inject constructor(
    private val tableRepository: TableRepository,
    private val userRepository: UserRepository,
    private val semesterStatusRepository: SemesterStatusRepository,
    private val displayMessageResolver: DisplayMessageResolver,
) : ViewModel() {
    private val _lectureReminderUiState = MutableStateFlow<LectureReminderUiState>(LectureReminderUiState.Loading)
    val lectureReminderUiState = _lectureReminderUiState.asStateFlow()

    private val updateEvent = MutableSharedFlow<LectureReminderChangeEvent>()

    private val _lectureReminderUiEvent: MutableSharedFlow<LectureReminderUiEvent> = MutableSharedFlow(replay = 1)
    val lectureReminderUiEvent = _lectureReminderUiEvent.asSharedFlow()

    private val currentTimetableId = MutableStateFlow("")
    private val primaryTimetableId = MutableStateFlow("")

    init {
        loadInitialData()
        handleUpdateEvents()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            semesterStatusRepository.semesterStatus
                .collectLatest { semesterStatus ->
                    if (semesterStatus == null) return@collectLatest
                    val targetYear =
                        semesterStatus.current?.year ?: semesterStatus.next.year
                    val targetSemester =
                        semesterStatus.current?.semester ?: semesterStatus.next.semester
                    _lectureReminderUiState.emit(LectureReminderUiState.Loading)
                    tableRepository.fetchTableList()
                        .onFailure {
                            _lectureReminderUiState.emit(LectureReminderUiState.Error)
                            return@collectLatest
                        }
                    val resolvedPrimaryId = tableRepository.tableSummaryList.value.firstOrNull { tableSummary ->
                        tableSummary.isPrimary && tableSummary.courseBook.year == targetYear && tableSummary.courseBook.semester == targetSemester
                    }?.id ?: run {
                        _lectureReminderUiState.emit(LectureReminderUiState.NoPrimaryTimetable)
                        return@collectLatest
                    }
                    tableRepository.getTimetableReminders(resolvedPrimaryId)
                        .onSuccess { data ->
                            currentTimetableId.emit(data.timetableId)
                            _lectureReminderUiState.emit(LectureReminderUiState.Success(data.lectureReminders.associateBy { it.lectureId }))
                        }
                        .onFailure {
                            _lectureReminderUiState.emit(LectureReminderUiState.Error)
                        }
                    primaryTimetableId.emit(resolvedPrimaryId)
                }
        }
    }

    private fun handleUpdateEvents() {
        viewModelScope.launch {
            updateEvent
                .debouncePerKey(200L) { changeEvent -> changeEvent.lectureId } // lectureId가 Key로 사용되어 lectureId가 서로 다른 변경은 debounce 없이 collect 한다.
                .distinctUntilChanged()
                .onEach { changeEvent ->
                    val lectureId = changeEvent.lectureId
                    val offset = changeEvent.option.lectureReminderOffset
                    tableRepository.updateTimetableLectureReminder(currentTimetableId.value, lectureId, offset)
                        .onSuccess {
                            _lectureReminderUiEvent.emit(
                                LectureReminderUiEvent.ShowSnackBarByEvent(
                                    when (offset) {
                                        LectureReminderOffset.NONE -> LectureReminderEvent.LECTURE_REMINDER_UPDATE_SUCCESS_NONE
                                        LectureReminderOffset.TEN_MINUTES_BEFORE -> LectureReminderEvent.LECTURE_REMINDER_UPDATE_SUCCESS_TEN_MINUTES_BEFORE
                                        LectureReminderOffset.AT_START_TIME -> LectureReminderEvent.LECTURE_REMINDER_UPDATE_SUCCESS_AT_START_TIME
                                        LectureReminderOffset.TEN_MINUTES_AFTER -> LectureReminderEvent.LECTURE_REMINDER_UPDATE_SUCCESS_TEN_MINUTES_AFTER
                                    },
                                ),
                            )
                        }
                        .onFailure { error ->
                            handleLectureReminderError(error)
                            changeEvent.onFallback()
                        }
                }
                .collect()
        }
    }

    private suspend fun handleLectureReminderError(error: DomainError) {
        val displayMessage = displayMessageResolver.getDisplayMessage(error)
        when (error) {
            is AuthError -> {
                _lectureReminderUiEvent.emit(LectureReminderUiEvent.ShowToast(displayMessage))
                userRepository.postForceLogout()
                _lectureReminderUiEvent.emit(LectureReminderUiEvent.LoggedOut)
            }

            else -> {
                _lectureReminderUiEvent.emit(LectureReminderUiEvent.ShowToast(displayMessage))
            }
        }
    }

    fun changeLectureReminderOption(lectureId: String, option: LectureWithReminderOption) {
        val previousState = _lectureReminderUiState.value
        _lectureReminderUiState.update { state ->
            when (state) {
                is LectureReminderUiState.Success -> state.copy(
                    data = state.data.toMutableMap().apply {
                        this[lectureId] = option
                    },
                )

                else -> state
            }
        }

        viewModelScope.launch {
            updateEvent.emit(
                LectureReminderChangeEvent(lectureId, option) {
                    _lectureReminderUiState.update { previousState }
                },
            )
        }
    }
}

sealed interface LectureReminderUiState {
    data object Loading : LectureReminderUiState
    data object Error : LectureReminderUiState
    data class Success(val data: Map<String, LectureWithReminderOption>) : LectureReminderUiState
    data object NoPrimaryTimetable : LectureReminderUiState
}

sealed interface LectureReminderUiEvent {
    data class ShowToast(val message: String) : LectureReminderUiEvent
    data class ShowSnackBarByEvent(val event: LectureReminderEvent) : LectureReminderUiEvent
    data object LoggedOut : LectureReminderUiEvent
}

enum class LectureReminderEvent {
    LECTURE_REMINDER_UPDATE_SUCCESS_NONE,
    LECTURE_REMINDER_UPDATE_SUCCESS_TEN_MINUTES_BEFORE,
    LECTURE_REMINDER_UPDATE_SUCCESS_AT_START_TIME,
    LECTURE_REMINDER_UPDATE_SUCCESS_TEN_MINUTES_AFTER,
}

private data class LectureReminderChangeEvent(
    val lectureId: String,
    val option: LectureWithReminderOption,
    val onFallback: suspend () -> Unit,
)
