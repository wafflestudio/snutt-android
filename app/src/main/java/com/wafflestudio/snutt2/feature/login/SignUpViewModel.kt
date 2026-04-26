package com.wafflestudio.snutt2.feature.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.data.onFailure
import com.wafflestudio.snutt2.data.onSuccess
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.domain.DisplayMessageResolver
import com.wafflestudio.snutt2.domain.DomainError
import com.wafflestudio.snutt2.domain.RefreshInitialDataUseCase
import com.wafflestudio.snutt2.logging.AnalyticsEvent
import com.wafflestudio.snutt2.logging.AnalyticsLogger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val refreshInitialDataUseCase: RefreshInitialDataUseCase,
    private val displayMessageResolver: DisplayMessageResolver,
    private val analyticsLogger: AnalyticsLogger,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<SignUpUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun onIdFieldChange(value: String) {
        _uiState.update { it.copy(idField = value) }
    }

    fun onPasswordFieldChange(value: String) {
        _uiState.update { it.copy(passwordField = value) }
    }

    fun onPasswordConfirmFieldChange(value: String) {
        _uiState.update { it.copy(passwordConfirmField = value) }
    }

    fun onEmailFieldChange(value: String) {
        _uiState.update { it.copy(emailField = value) }
    }

    fun signUp(formattedEmail: String) {
        val state = _uiState.value
        viewModelScope.launch {
            analyticsLogger.logEvent(AnalyticsEvent.SignUp)
            _uiState.update { it.copy(isLoading = true) }
            userRepository.postSignUp(state.idField, state.passwordField, formattedEmail)
                .onSuccess {
                    refreshInitialDataUseCase()
                    _uiEvent.emit(SignUpUiEvent.NavigateEmailVerification)
                }
                .onFailure { handleError(it) }
        }
    }

    private suspend fun handleError(error: DomainError) {
        _uiState.update { it.copy(isLoading = false) }
        _uiEvent.emit(SignUpUiEvent.ShowToast(displayMessageResolver.getDisplayMessage(error)))
    }
}

data class SignUpUiState(
    val idField: String = "",
    val passwordField: String = "",
    val passwordConfirmField: String = "",
    val emailField: String = "",
    val isLoading: Boolean = false,
)

sealed interface SignUpUiEvent {
    data class ShowToast(val message: String) : SignUpUiEvent
    data object NavigateEmailVerification : SignUpUiEvent
}
