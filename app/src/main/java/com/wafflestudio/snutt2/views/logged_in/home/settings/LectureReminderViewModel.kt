package com.wafflestudio.snutt2.views.logged_in.home.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.components.compose.debouncePerKey
import com.wafflestudio.snutt2.data.tables.TableRepository
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.lib.network.AuthError
import com.wafflestudio.snutt2.lib.network.DisplayMessageResolver
import com.wafflestudio.snutt2.lib.network.DomainError
import com.wafflestudio.snutt2.lib.network.onFailure
import com.wafflestudio.snutt2.lib.network.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LectureReminderViewModel @Inject constructor(
    private val tableRepository: TableRepository,
    private val userRepository: UserRepository,
    private val displayMessageResolver: DisplayMessageResolver,
) : ViewModel() {
    private val _lectureReminderUiState = MutableStateFlow<LectureReminderUiState>(LectureReminderUiState.Loading)
    val lectureReminderUiState = _lectureReminderUiState.asStateFlow()

    private val _updateEvent = MutableSharedFlow<Pair<String, LectureWithReminderOption>>()

    private val _lectureReminderUiEvent: MutableSharedFlow<LectureReminderUiEvent> = MutableSharedFlow(replay = 1)
    val lectureReminderUiEvent = _lectureReminderUiEvent.asSharedFlow()

    init {
        loadInitialData()
        handleUpdateEvents()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            tableRepository.getPrimaryTableLectureReminders()
                .onSuccess { data ->
                    _lectureReminderUiState.emit(LectureReminderUiState.Success(data.associateBy { it.lectureId }))
                }
                .onFailure {
                    _lectureReminderUiState.emit(LectureReminderUiState.Error)
                }
        }
    }

    private fun handleUpdateEvents() {
        viewModelScope.launch {
            _updateEvent
                .debouncePerKey(200L) { (lectureId, _) -> lectureId } // lectureId가 Key로 사용되어 lectureId가 서로 다른 변경은 debounce 없이 collect 한다.
                .distinctUntilChanged()
                .onEach { (lectureId, option) ->
                    tableRepository.updateTableLectureReminders(lectureId, option)
                        .onFailure { error ->
                            handleLectureReminderError(error)
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
                _lectureReminderUiEvent.emit(LectureReminderUiEvent.NavigateToOnboard)
            }
            else -> {
                _lectureReminderUiEvent.emit(LectureReminderUiEvent.ShowToast(displayMessage))
            }
        }
    }

    fun changeLectureReminderOption(lectureId: String, option: LectureWithReminderOption) {
        _lectureReminderUiState.update { currentState ->
            if (currentState is LectureReminderUiState.Success) {
                currentState.copy(
                    data = currentState.data.toMutableMap().apply {
                        this[lectureId] = option
                    },
                )
            } else {
                currentState
            }
        }

        viewModelScope.launch {
            _updateEvent.emit(lectureId to option)
        }
    }
}

sealed interface LectureReminderUiState {
    data object Loading : LectureReminderUiState
    data object Error : LectureReminderUiState
    data class Success(val data: Map<String, LectureWithReminderOption>) : LectureReminderUiState
}

data class LectureWithReminderOption(
    val lectureId: String,
    val lectureTitle: String,
    val lectureReminderOffset: LectureReminderOffset,
)

enum class LectureReminderOffset {
    NONE, TEN_MINUTES_BEFORE, AT_START_TIME, TEN_MINUTES_AFTER
}

sealed interface LectureReminderUiEvent {
    data class ShowToast(val message: String) : LectureReminderUiEvent
    data class ShowSnackBar(val message: String) : LectureReminderUiEvent
    data object NavigateToOnboard : LectureReminderUiEvent
}
