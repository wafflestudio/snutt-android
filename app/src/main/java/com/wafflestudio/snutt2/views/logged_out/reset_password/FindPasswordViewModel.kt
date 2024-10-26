package com.wafflestudio.snutt2.views.logged_out.reset_password

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.wafflestudio.snutt2.data.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class FindPasswordViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UIState>(UIState.CheckId(""))
    val uiState: StateFlow<UIState> = _uiState

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

        data object EnterNewPassword : UIState()
    }

    fun goToPreviousStep() {
        when (_uiState.value) {
            is UIState.CheckId -> {}
            is UIState.EnterFullEmail -> {
                val savedUserId = savedStateHandle["userId"] ?: ""
                _uiState.value = UIState.CheckId(savedUserId)
            }
            is UIState.VerifyCode -> {
                val savedUserId = savedStateHandle["userId"] ?: ""
                val savedMaskedEmail = savedStateHandle["maskedEmail"] ?: ""
                val savedFullEmail = savedStateHandle["fullEmail"] ?: ""
                _uiState.value = UIState.EnterFullEmail(savedUserId, savedMaskedEmail, savedFullEmail)
            }
            else -> {}
        }
    }

    suspend fun checkEmailById(userId: String) {
        savedStateHandle["userId"] = userId
        val maskedEmail = userRepository.checkEmailById(userId)
        savedStateHandle["maskedEmail"] = maskedEmail
        val savedFullEmail = savedStateHandle["fullEmail"] ?: ""
        _uiState.value = UIState.EnterFullEmail(userId, maskedEmail, savedFullEmail)
    }

    suspend fun sendFullEmailAndRequestCode(fullEmail: String) {
        savedStateHandle["fullEmail"] = fullEmail
        userRepository.sendPwResetCodeToEmail(fullEmail)
        _uiState.value = UIState.VerifyCode(fullEmail)
    }

    suspend fun verifyCode(code: String) {}
}
