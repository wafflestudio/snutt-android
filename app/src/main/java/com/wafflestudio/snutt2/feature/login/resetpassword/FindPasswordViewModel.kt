package com.wafflestudio.snutt2.feature.login.resetpassword

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.data.onFailure
import com.wafflestudio.snutt2.data.onSuccess
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.domain.DisplayMessageResolver
import com.wafflestudio.snutt2.domain.DomainError
import com.wafflestudio.snutt2.lib.isPasswordInvalid
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FindPasswordViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val savedStateHandle: SavedStateHandle,
    private val displayMessageResolver: DisplayMessageResolver,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UIState>(UIState.CheckId(""))
    val uiState: StateFlow<UIState> = _uiState

    private val _uiEvent = MutableSharedFlow<FindPasswordUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    sealed class UIState {
        data class CheckId(
            val userId: String,
        ) : UIState()

        data class EnterFullEmail(
            val userId: String,
            val maskedEmail: String,
            val fullEmail: String,
        ) : UIState()

        data class VerifyCode(
            val fullEmail: String,
            val codeField: String = "",
            val dialogState: VerifyCodeDialogState = VerifyCodeDialogState.None,
        ) : UIState() {
            sealed interface VerifyCodeDialogState {
                data object None : VerifyCodeDialogState
                data object WhyNotCodeComing : VerifyCodeDialogState
            }
        }

        data class EnterNewPassword(
            val newPasswordField: String = "",
            val newPasswordConfirmField: String = "",
            val dialogState: NewPasswordDialogState = NewPasswordDialogState.None,
        ) : UIState() {
            sealed interface NewPasswordDialogState {
                data object None : NewPasswordDialogState
                data class Error(val type: ErrorType) : NewPasswordDialogState
                data object Complete : NewPasswordDialogState
            }

            enum class ErrorType { Expired, ConfirmFail, InvalidPassword }
        }
    }

    fun onIdFieldChange(value: String) {
        _uiState.update {
            if (it is UIState.CheckId) it.copy(userId = value) else it
        }
    }

    fun onEmailFieldChange(value: String) {
        _uiState.update {
            if (it is UIState.EnterFullEmail) it.copy(fullEmail = value) else it
        }
    }

    fun onCodeFieldChange(value: String) {
        _uiState.update {
            if (it is UIState.VerifyCode) it.copy(codeField = value) else it
        }
    }

    fun onNewPasswordFieldChange(value: String) {
        _uiState.update {
            if (it is UIState.EnterNewPassword) it.copy(newPasswordField = value) else it
        }
    }

    fun onNewPasswordConfirmFieldChange(value: String) {
        _uiState.update {
            if (it is UIState.EnterNewPassword) it.copy(newPasswordConfirmField = value) else it
        }
    }

    fun showWhyNotCodeComingDialog() {
        _uiState.update {
            if (it is UIState.VerifyCode) it.copy(dialogState = UIState.VerifyCode.VerifyCodeDialogState.WhyNotCodeComing) else it
        }
    }

    fun dismissVerifyCodeDialog() {
        _uiState.update {
            if (it is UIState.VerifyCode) it.copy(dialogState = UIState.VerifyCode.VerifyCodeDialogState.None) else it
        }
    }

    fun onTimerExpired() {
        _uiState.update {
            if (it is UIState.EnterNewPassword) {
                it.copy(dialogState = UIState.EnterNewPassword.NewPasswordDialogState.Error(UIState.EnterNewPassword.ErrorType.Expired))
            } else {
                it
            }
        }
    }

    fun dismissNewPasswordDialog() {
        _uiState.update {
            if (it is UIState.EnterNewPassword) {
                it.copy(dialogState = UIState.EnterNewPassword.NewPasswordDialogState.None)
            } else {
                it
            }
        }
    }

    fun goToPreviousStep() {
        when (_uiState.value) {
            is UIState.CheckId -> {}
            is UIState.EnterFullEmail -> {
                val savedUserId = savedStateHandle["userId"] ?: ""
                _uiState.update { UIState.CheckId(savedUserId) }
            }

            is UIState.VerifyCode -> {
                val savedUserId = savedStateHandle["userId"] ?: ""
                val savedMaskedEmail = savedStateHandle["maskedEmail"] ?: ""
                val savedFullEmail = savedStateHandle["fullEmail"] ?: ""
                _uiState.update { UIState.EnterFullEmail(savedUserId, savedMaskedEmail, savedFullEmail) }
            }

            is UIState.EnterNewPassword -> {
                val savedUserId = savedStateHandle["userId"] ?: ""
                _uiState.update { UIState.CheckId(savedUserId) }
            }
        }
    }

    fun checkEmailById() {
        val state = _uiState.value as? UIState.CheckId ?: return
        val userId = state.userId
        viewModelScope.launch {
            savedStateHandle["userId"] = userId
            userRepository.checkEmailById(userId)
                .onSuccess { maskedEmail ->
                    savedStateHandle["maskedEmail"] = maskedEmail
                    val savedFullEmail = savedStateHandle["fullEmail"] ?: ""
                    _uiState.update { UIState.EnterFullEmail(userId, maskedEmail, savedFullEmail) }
                }
                .onFailure { handleError(it) }
        }
    }

    fun sendFullEmailAndRequestCode() {
        val state = _uiState.value as? UIState.EnterFullEmail ?: return
        val fullEmail = state.fullEmail
        viewModelScope.launch {
            savedStateHandle["fullEmail"] = fullEmail
            userRepository.sendPwResetCodeToEmail(fullEmail)
                .onSuccess {
                    _uiState.update { UIState.VerifyCode(fullEmail) }
                }
                .onFailure { handleError(it) }
        }
    }

    fun resendVerifyCode() {
        val state = _uiState.value as? UIState.VerifyCode ?: return
        viewModelScope.launch {
            userRepository.sendPwResetCodeToEmail(state.fullEmail)
                .onFailure { handleError(it) }
        }
    }

    fun verifyCode() {
        val state = _uiState.value as? UIState.VerifyCode ?: return
        val code = state.codeField
        viewModelScope.launch {
            val savedUserId = savedStateHandle["userId"] ?: ""
            userRepository.verifyPwResetCode(savedUserId, code)
                .onSuccess {
                    savedStateHandle["code"] = code
                    _uiState.update { UIState.EnterNewPassword() }
                }
                .onFailure { handleError(it) }
        }
    }

    fun validateAndResetPassword(timerRunning: Boolean) {
        val state = _uiState.value as? UIState.EnterNewPassword ?: return
        if (!timerRunning) return
        val password = state.newPasswordField
        if (password != state.newPasswordConfirmField) {
            _uiState.update {
                (it as UIState.EnterNewPassword).copy(
                    dialogState = UIState.EnterNewPassword.NewPasswordDialogState.Error(UIState.EnterNewPassword.ErrorType.ConfirmFail),
                )
            }
        } else if (password.isPasswordInvalid()) {
            _uiState.update {
                (it as UIState.EnterNewPassword).copy(
                    dialogState = UIState.EnterNewPassword.NewPasswordDialogState.Error(UIState.EnterNewPassword.ErrorType.InvalidPassword),
                )
            }
        } else {
            resetPassword(password)
        }
    }

    private fun resetPassword(password: String) {
        viewModelScope.launch {
            val savedUserId = savedStateHandle["userId"] ?: ""
            val savedCode = savedStateHandle["code"] ?: ""
            userRepository.resetPassword(savedUserId, password, savedCode)
                .onSuccess {
                    _uiState.update {
                        if (it is UIState.EnterNewPassword) {
                            it.copy(dialogState = UIState.EnterNewPassword.NewPasswordDialogState.Complete)
                        } else {
                            it
                        }
                    }
                }
                .onFailure { handleError(it) }
        }
    }

    fun onCompleteDialogConfirm() {
        viewModelScope.launch {
            _uiEvent.emit(FindPasswordUiEvent.NavigateBack)
        }
    }

    private suspend fun handleError(error: DomainError) {
        _uiEvent.emit(FindPasswordUiEvent.ShowToast(displayMessageResolver.getDisplayMessage(error)))
    }
}

sealed interface FindPasswordUiEvent {
    data class ShowToast(val message: String) : FindPasswordUiEvent
    data object NavigateBack : FindPasswordUiEvent
}
