package com.wafflestudio.snutt2.views.logged_out

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.domain.RefreshInitialDataUseCase
import com.wafflestudio.snutt2.lib.logging.AnalyticsEvent
import com.wafflestudio.snutt2.lib.logging.AnalyticsLogger
import com.wafflestudio.snutt2.lib.logging.LoginParameter
import com.wafflestudio.snutt2.lib.network.DisplayMessageResolver
import com.wafflestudio.snutt2.lib.network.DomainError
import com.wafflestudio.snutt2.lib.network.onFailure
import com.wafflestudio.snutt2.lib.network.onSuccess
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
class SignInViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val refreshInitialDataUseCase: RefreshInitialDataUseCase,
    private val displayMessageResolver: DisplayMessageResolver,
    private val analyticsLogger: AnalyticsLogger,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignInUiState())
    val uiState: StateFlow<SignInUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<SignInUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun signIn(id: String, password: String) {
        viewModelScope.launch {
            analyticsLogger.logEvent(AnalyticsEvent.Login(LoginParameter(LoginParameter.Provider.LOCAL)))
            _uiState.update { it.copy(isLoading = true) }
            userRepository.postSignIn(id, password)
                .onSuccess {
                    refreshInitialDataUseCase()
                    _uiEvent.emit(SignInUiEvent.NavigateHome)
                }
                .onFailure { handleError(it) }
        }
    }

    private suspend fun handleError(error: DomainError) {
        _uiState.update { it.copy(isLoading = false) }
        _uiEvent.emit(SignInUiEvent.ShowToast(displayMessageResolver.getDisplayMessage(error)))
    }
}

data class SignInUiState(
    val isLoading: Boolean = false,
)

sealed interface SignInUiEvent {
    data class ShowToast(val message: String) : SignInUiEvent
    data object NavigateHome : SignInUiEvent
}
