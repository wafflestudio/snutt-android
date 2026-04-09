package com.wafflestudio.snutt2.feature.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.domain.DisplayMessageResolver
import com.wafflestudio.snutt2.domain.DomainError
import com.wafflestudio.snutt2.data.onFailure
import com.wafflestudio.snutt2.data.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmailVerificationViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val displayMessageResolver: DisplayMessageResolver,
) : ViewModel() {

    private val _uiEvent = MutableSharedFlow<EmailVerificationUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    val userEmail: String = userRepository.user.value?.email ?: ""

    fun sendCodeToEmail(email: String) {
        viewModelScope.launch {
            userRepository.sendCodeToEmail(email)
                .onSuccess {
                    _uiEvent.emit(EmailVerificationUiEvent.CodeSent)
                }
                .onFailure { handleError(it) }
        }
    }

    fun verifyEmailCode(code: String) {
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

sealed interface EmailVerificationUiEvent {
    data class ShowToast(val message: String) : EmailVerificationUiEvent
    data object CodeSent : EmailVerificationUiEvent
    data object VerificationSuccess : EmailVerificationUiEvent
}
