package com.wafflestudio.snutt2.views.logged_in.home.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.domainmodel.PushPreferenceType
import com.wafflestudio.snutt2.lib.network.call_adapter.ErrorParsedHttpException
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
) : ViewModel() {
    private val _pushPreferencesUiState =
        MutableStateFlow<PushPreferencesUiState>(PushPreferencesUiState.Loading)
    val pushPreferenceUiState = _pushPreferencesUiState.asStateFlow()

    private val _pushPreferencesUiEvent: MutableSharedFlow<PushPreferencesUiEvent> = MutableSharedFlow(replay = 1)
    val pushPreferencesUiEvent = _pushPreferencesUiEvent.asSharedFlow()

    fun loadPushPreferences() {
        viewModelScope.launch {
            runCatching {
                _pushPreferencesUiState.emit(PushPreferencesUiState.Success(userRepository.getPushPreferences()))
            }.onFailure { e ->
                if (e is ErrorParsedHttpException) {
                    _pushPreferencesUiState.emit(PushPreferencesUiState.Error)
                    _pushPreferencesUiEvent.emit(PushPreferencesUiEvent.ShowToast(e.errorDTO?.displayMessage ?: ""))
                }
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
                }
                _pushPreferencesUiState.emit(PushPreferencesUiState.Success(updatedPrefs))
            }
        }
    }

    fun postPushPreferences() {
        viewModelScope.launch {
            val currentState = _pushPreferencesUiState.value
            if (currentState is PushPreferencesUiState.Success) {
                runCatching {
                    userRepository.postPushPreferences(currentState.pushPreferences)
                }.onFailure { e ->
                    if (e is ErrorParsedHttpException) {
                        _pushPreferencesUiState.emit(PushPreferencesUiState.Error)
                        _pushPreferencesUiEvent.emit(PushPreferencesUiEvent.ShowToast(e.errorDTO?.displayMessage ?: ""))
                    }
                }
            }
        }
    }
}
