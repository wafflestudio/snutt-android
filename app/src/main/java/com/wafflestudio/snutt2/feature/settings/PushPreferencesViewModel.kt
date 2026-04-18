package com.wafflestudio.snutt2.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.data.onFailure
import com.wafflestudio.snutt2.data.onSuccess
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.domain.AuthError
import com.wafflestudio.snutt2.domain.DisplayMessageResolver
import com.wafflestudio.snutt2.domain.DomainError
import com.wafflestudio.snutt2.domain.model.PushPreferenceType
import com.wafflestudio.snutt2.domain.model.PushPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PushPreferencesViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val displayMessageResolver: DisplayMessageResolver,
) : ViewModel() {
    private val _pushPreferencesUiState =
        MutableStateFlow<PushPreferencesUiState>(PushPreferencesUiState.Loading)
    val pushPreferencesUiState = _pushPreferencesUiState.asStateFlow()

    private val _pushPreferencesUiEvent: MutableSharedFlow<PushPreferencesUiEvent> = MutableSharedFlow(replay = 1)
    val pushPreferencesUiEvent = _pushPreferencesUiEvent.asSharedFlow()

    fun loadPushPreferences() {
        viewModelScope.launch {
            _pushPreferencesUiState.emit(PushPreferencesUiState.Loading)
            userRepository.getPushPreferences()
                .onSuccess { data ->
                    _pushPreferencesUiState.emit(PushPreferencesUiState.Success(data))
                }
                .onFailure { error ->
                    _pushPreferencesUiState.emit(PushPreferencesUiState.Error)
                    handlePushPreferencesError(error)
                }
        }
    }

    fun togglePushPreferences(type: PushPreferenceType) {
        viewModelScope.launch {
            val currentState = _pushPreferencesUiState.value
            if (currentState is PushPreferencesUiState.Success) {
                val currentPrefs = currentState.pushPreferences
                val updatedPrefs = when (type) {
                    PushPreferenceType.LECTURE_UPDATE -> currentPrefs.copy(
                        lectureUpdate = !currentPrefs.lectureUpdate,
                    )

                    PushPreferenceType.VACANCY_NOTIFICATION -> currentPrefs.copy(
                        vacancyNotification = !currentPrefs.vacancyNotification,
                    )

                    PushPreferenceType.DIARY -> currentPrefs.copy(
                        lectureDiary = !currentPrefs.lectureDiary,
                    )
                }

                userRepository.postPushPreferences(updatedPrefs)
                    .onSuccess {
                        _pushPreferencesUiState.emit(PushPreferencesUiState.Success(updatedPrefs))
                    }
                    .onFailure { error ->
                        _pushPreferencesUiState.emit(PushPreferencesUiState.Error)
                        handlePushPreferencesError(error)
                    }
            }
        }
    }

    private suspend fun handlePushPreferencesError(error: DomainError) {
        val displayMessage = displayMessageResolver.getDisplayMessage(error)
        when (error) {
            is AuthError -> {
                _pushPreferencesUiEvent.emit(PushPreferencesUiEvent.ShowToast(displayMessage))
                userRepository.postForceLogout()
                _pushPreferencesUiEvent.emit(PushPreferencesUiEvent.NavigateToOnboard)
            }

            else -> {
                _pushPreferencesUiEvent.emit(PushPreferencesUiEvent.ShowToast(displayMessage))
            }
        }
    }
}

sealed interface PushPreferencesUiState {
    data object Loading : PushPreferencesUiState
    data class Success(val pushPreferences: PushPreferences) : PushPreferencesUiState
    data object Error : PushPreferencesUiState
}

sealed interface PushPreferencesUiEvent {
    data class ShowToast(val message: String) : PushPreferencesUiEvent
    data object NavigateToOnboard : PushPreferencesUiEvent
}
