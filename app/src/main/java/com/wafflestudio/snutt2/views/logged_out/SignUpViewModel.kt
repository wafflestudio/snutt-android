package com.wafflestudio.snutt2.views.logged_out

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.domain.RefreshInitialDataUseCase
import com.wafflestudio.snutt2.lib.logging.AnalyticsEvent
import com.wafflestudio.snutt2.lib.logging.AnalyticsLogger
import com.wafflestudio.snutt2.domain.DisplayMessageResolver
import com.wafflestudio.snutt2.domain.DomainError
import com.wafflestudio.snutt2.data.onFailure
import com.wafflestudio.snutt2.data.onSuccess
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

    fun signUp(id: String, email: String, password: String) {
        viewModelScope.launch {
            analyticsLogger.logEvent(AnalyticsEvent.SignUp)
            _uiState.update { it.copy(isLoading = true) }
            userRepository.postSignUp(id, password, email)
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
    val isLoading: Boolean = false,
)

sealed interface SignUpUiEvent {
    data class ShowToast(val message: String) : SignUpUiEvent
    data object NavigateEmailVerification : SignUpUiEvent
}
