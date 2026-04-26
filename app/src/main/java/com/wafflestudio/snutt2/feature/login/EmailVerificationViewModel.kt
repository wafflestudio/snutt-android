package com.wafflestudio.snutt2.feature.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.data.onFailure
import com.wafflestudio.snutt2.data.onSuccess
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.domain.DisplayMessageResolver
import com.wafflestudio.snutt2.domain.DomainError
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
class EmailVerificationViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val displayMessageResolver: DisplayMessageResolver,
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmailVerificationUiState())
    val uiState: StateFlow<EmailVerificationUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<EmailVerificationUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    val userEmail: String = userRepository.user.value?.email ?: ""

    fun onCodeFieldChange(value: String) {
        _uiState.update { it.copy(codeField = value) }
    }

    fun backToAskContinue() {
        _uiState.update { it.copy(flowState = EmailVerificationUiState.FlowState.AskContinue, codeField = "") }
    }

    fun sendCodeToEmail() {
        viewModelScope.launch {
            userRepository.sendCodeToEmail(userEmail)
                .onSuccess {
                    _uiState.update { it.copy(flowState = EmailVerificationUiState.FlowState.SendCode) }
                    _uiEvent.emit(EmailVerificationUiEvent.RestartTimer)
                }
                .onFailure { handleError(it) }
        }
    }

    fun verifyEmailCode() {
        val code = _uiState.value.codeField
        viewModelScope.launch {
            userRepository.verifyEmailCode(code)
                .onSuccess {
                    _uiEvent.emit(EmailVerificationUiEvent.VerificationSuccess)
                }
                .onFailure { handleError(it) }
        }
    }

    private suspend fun handleError(error: DomainError) {
        _uiEvent.emit(EmailVerificationUiEvent.ShowToast(displayMessageResolver.getDisplayMessage(error)))
    }
}

data class EmailVerificationUiState(
    val flowState: FlowState = FlowState.AskContinue,
    val codeField: String = "",
) {
    enum class FlowState { AskContinue, SendCode }
}

sealed interface EmailVerificationUiEvent {
    data class ShowToast(val message: String) : EmailVerificationUiEvent
    data object RestartTimer : EmailVerificationUiEvent
    data object VerificationSuccess : EmailVerificationUiEvent
}
