package com.wafflestudio.snutt2.views.logged_out.reset_password

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.lib.network.DisplayMessageResolver
import com.wafflestudio.snutt2.lib.network.DomainError
import com.wafflestudio.snutt2.lib.network.onFailure
import com.wafflestudio.snutt2.lib.network.onSuccess
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
        ) : UIState()

        data class EnterNewPassword(
            val showCompleteDialog: Boolean = false,
        ) : UIState()
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

    fun checkEmailById(userId: String) {
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

    fun sendFullEmailAndRequestCode(fullEmail: String) {
        viewModelScope.launch {
            savedStateHandle["fullEmail"] = fullEmail
            userRepository.sendPwResetCodeToEmail(fullEmail)
                .onSuccess {
                    _uiState.update { UIState.VerifyCode(fullEmail) }
                }
                .onFailure { handleError(it) }
        }
    }

    fun verifyCode(code: String) {
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

    fun resetPassword(password: String) {
        viewModelScope.launch {
            val savedUserId = savedStateHandle["userId"] ?: ""
            val savedCode = savedStateHandle["code"] ?: ""
            userRepository.resetPassword(savedUserId, password, savedCode)
                .onSuccess {
                    _uiState.update { UIState.EnterNewPassword(showCompleteDialog = true) }
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
