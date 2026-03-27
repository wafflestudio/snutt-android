package com.wafflestudio.snutt2.views.logged_out

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.domain.RefreshInitialDataUseCase
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
class TutorialViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val refreshInitialDataUseCase: RefreshInitialDataUseCase,
    private val displayMessageResolver: DisplayMessageResolver,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TutorialUiState())
    val uiState: StateFlow<TutorialUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<TutorialUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun onFacebookLoginRequested() {
        viewModelScope.launch {
            _uiEvent.emit(TutorialUiEvent.LaunchFacebookLogin)
        }
    }

    fun onFacebookTokenReceived(token: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            userRepository.postLoginFacebookNew(token)
                .onSuccess {
                    refreshInitialDataUseCase()
                    _uiEvent.emit(TutorialUiEvent.NavigateHome)
                }
                .onFailure { handleError(it) }
        }
    }

    fun onGoogleLoginRequested() {
        viewModelScope.launch {
            _uiEvent.emit(TutorialUiEvent.LaunchGoogleSignIn)
        }
    }

    fun onGoogleAuthCodeReceived(authCode: String, clientId: String, clientSecret: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            userRepository.getAccessTokenByAuthCodeNew(authCode, clientId, clientSecret)
                .onSuccess { googleAccessToken ->
                    userRepository.postLoginGoogleNew(googleAccessToken)
                        .onSuccess {
                            refreshInitialDataUseCase()
                            _uiEvent.emit(TutorialUiEvent.NavigateHome)
                        }
                        .onFailure { handleError(it) }
                }
                .onFailure { handleError(it) }
        }
    }

    fun onKakaoLoginRequested() {
        viewModelScope.launch {
            _uiEvent.emit(TutorialUiEvent.LaunchKakaoLogin)
        }
    }

    fun onKakaoTokenReceived(token: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            userRepository.postLoginKakaoNew(token)
                .onSuccess {
                    refreshInitialDataUseCase()
                    _uiEvent.emit(TutorialUiEvent.NavigateHome)
                }
                .onFailure { handleError(it) }
        }
    }

    private suspend fun handleError(error: DomainError) {
        _uiState.update { it.copy(isLoading = false) }
        _uiEvent.emit(TutorialUiEvent.ShowToast(displayMessageResolver.getDisplayMessage(error)))
    }
}

data class TutorialUiState(
    val isLoading: Boolean = false,
)

sealed interface TutorialUiEvent {
    data class ShowToast(val message: String) : TutorialUiEvent
    data object NavigateHome : TutorialUiEvent
    data object LaunchGoogleSignIn : TutorialUiEvent
    data object LaunchFacebookLogin : TutorialUiEvent
    data object LaunchKakaoLogin : TutorialUiEvent
}
