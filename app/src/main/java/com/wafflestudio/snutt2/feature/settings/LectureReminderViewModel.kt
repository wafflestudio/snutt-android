package com.wafflestudio.snutt2.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.data.onFailure
import com.wafflestudio.snutt2.data.onSuccess
import com.wafflestudio.snutt2.data.semesterstatus.SemesterStatusRepository
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
import kotlinx.coroutines.flow.StateFlow
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
    private val _uiEvent = MutableSharedFlow<LectureReminderUiEvent>(replay = 1)
    val uiEvent = _uiEvent.asSharedFlow()

    private val _uiState: MutableStateFlow<LectureReminderUiState> = MutableStateFlow(LectureReminderUiState.Loading)
    val uiState: StateFlow<LectureReminderUiState> = _uiState.asStateFlow()

    private val updateEvent = MutableSharedFlow<LectureReminderChangeEvent>()

    init {
        loadInitialData()
        pushReminderUpdate()
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
                    _uiState.update { LectureReminderUiState.Loading }
                    tableRepository.fetchTableList()
                        .onFailure {
                            _uiState.update { LectureReminderUiState.Error }
                            return@collectLatest
                        }
                    val resolvedPrimaryId = tableRepository.tableSummaryList.value.firstOrNull { tableSummary ->
                        tableSummary.isPrimary && tableSummary.courseBook.year == targetYear && tableSummary.courseBook.semester == targetSemester
                    }?.id ?: run {
                        _uiState.update { LectureReminderUiState.NoPrimaryTimetable }
                        return@collectLatest
                    }
                    tableRepository.getTimetableReminders(resolvedPrimaryId)
                        .onSuccess { data ->
                            _uiState.update {
                                LectureReminderUiState.Success(
                                    data = data.lectureReminders.associateBy { it.lectureId },
                                    timetableId = data.timetableId,
                                )
                            }
                        }
                        .onFailure {
                            _uiState.update { LectureReminderUiState.Error }
                        }
                }
        }
    }

    private fun pushReminderUpdate() {
        viewModelScope.launch {
            updateEvent
                .debouncePerKey(200L) { changeEvent -> changeEvent.lectureId } // lectureId가 Key로 사용되어 lectureId가 서로 다른 변경은 debounce 없이 collect 한다.
                .distinctUntilChanged()
                .onEach { changeEvent ->
                    val timetableId = (_uiState.value as? LectureReminderUiState.Success)?.timetableId
                        ?: return@onEach
                    val lectureId = changeEvent.lectureId
                    val offset = changeEvent.option.lectureReminderOffset
                    tableRepository.updateTimetableLectureReminder(timetableId, lectureId, offset)
                        .onSuccess {
                            _uiEvent.emit(LectureReminderUiEvent.ShowUpdateSuccessSnackBar(offset))
                        }
                        .onFailure { error ->
                            handleLectureReminderError(error)
                            rollbackLectureReminderOption(changeEvent.lectureId, changeEvent.previousOption)
                        }
                }
                .collect()
        }
    }

    private suspend fun handleLectureReminderError(error: DomainError) {
        val displayMessage = displayMessageResolver.getDisplayMessage(error)
        when (error) {
            is AuthError -> {
                _uiEvent.emit(LectureReminderUiEvent.ShowToast(displayMessage))
                userRepository.postForceLogout()
                _uiEvent.emit(LectureReminderUiEvent.LoggedOut)
            }

            else -> {
                _uiEvent.emit(LectureReminderUiEvent.ShowToast(displayMessage))
            }
        }
    }

    fun updateReminderOption(lectureId: String, option: LectureWithReminderOption) {
        val currentState = _uiState.value as? LectureReminderUiState.Success ?: return
        val previousOption = currentState.data[lectureId] ?: return

        _uiState.update { state ->
            when (state) {
                is LectureReminderUiState.Success -> state.copy(
                    data = state.data + (lectureId to option),
                )

                else -> state
            }
        }

        viewModelScope.launch {
            updateEvent.emit(LectureReminderChangeEvent(lectureId, option, previousOption))
        }
    }

    private fun rollbackLectureReminderOption(lectureId: String, previousOption: LectureWithReminderOption) {
        _uiState.update { state ->
            when (state) {
                is LectureReminderUiState.Success -> state.copy(
                    data = state.data + (lectureId to previousOption),
                )

                else -> state
            }
        }
    }
}

sealed interface LectureReminderUiState {
    data object Loading : LectureReminderUiState
    data object Error : LectureReminderUiState
    data class Success(
        val data: Map<String, LectureWithReminderOption>,
        val timetableId: String,
    ) : LectureReminderUiState
    data object NoPrimaryTimetable : LectureReminderUiState
}

sealed interface LectureReminderUiEvent {
    data class ShowToast(val message: String) : LectureReminderUiEvent
    data class ShowUpdateSuccessSnackBar(val offset: LectureReminderOffset) : LectureReminderUiEvent
    data object LoggedOut : LectureReminderUiEvent
}

private data class LectureReminderChangeEvent(
    val lectureId: String,
    val option: LectureWithReminderOption,
    val previousOption: LectureWithReminderOption,
)
