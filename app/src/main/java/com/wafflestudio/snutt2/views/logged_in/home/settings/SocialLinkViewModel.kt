package com.wafflestudio.snutt2.views.logged_in.home.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.facebook.login.LoginManager
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.domainmodel.SocialProviders
import com.wafflestudio.snutt2.lib.network.DisplayMessageResolver
import com.wafflestudio.snutt2.lib.network.DomainError
import com.wafflestudio.snutt2.lib.network.onFailure
import com.wafflestudio.snutt2.lib.network.onSuccess
import com.wafflestudio.snutt2.model.SocialLoginType
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
class SocialLinkViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val displayMessageResolver: DisplayMessageResolver,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SocialLinkUiState())
    val uiState: StateFlow<SocialLinkUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<SocialLinkUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        fetchSocialProviders()
    }

    // region 소셜 연동 요청 → UiEvent로 SDK 실행 위임

    fun onFacebookConnectRequested() {
        viewModelScope.launch {
            _uiEvent.emit(SocialLinkUiEvent.LaunchFacebookLogin)
        }
    }

    fun onFacebookTokenReceived(token: String) {
        viewModelScope.launch {
            userRepository.postUserFacebook(token)
                .onSuccess { refreshAfterConnect() }
                .onFailure { handleError(it) }
        }
    }

    fun onGoogleConnectRequested() {
        viewModelScope.launch {
            _uiEvent.emit(SocialLinkUiEvent.LaunchGoogleSignIn)
        }
    }

    fun onGoogleAuthCodeReceived(authCode: String, clientId: String, clientSecret: String) {
        viewModelScope.launch {
            userRepository.getAccessTokenByAuthCode(authCode, clientId, clientSecret)
                .onSuccess { googleAccessToken ->
                    userRepository.postUserGoogle(googleAccessToken)
                        .onSuccess { refreshAfterConnect() }
                        .onFailure { handleError(it) }
                }
                .onFailure { handleError(it) }
        }
    }

    fun onKakaoConnectRequested() {
        viewModelScope.launch {
            _uiEvent.emit(SocialLinkUiEvent.LaunchKakaoLogin)
        }
    }

    fun onKakaoTokenReceived(token: String) {
        viewModelScope.launch {
            userRepository.postUserKakao(token)
                .onSuccess { refreshAfterConnect() }
                .onFailure { handleError(it) }
        }
    }

    // endregion

    // region 연동 해제

    fun showDisconnectDialog(type: SocialLoginType) {
        _uiState.update { it.copy(disconnectDialogType = type) }
    }

    fun dismissDisconnectDialog() {
        _uiState.update { it.copy(disconnectDialogType = SocialLoginType.NONE) }
    }

    fun confirmDisconnect() {
        val type = _uiState.value.disconnectDialogType
        viewModelScope.launch {
            LoginManager.getInstance().logOut()
            val result = when (type) {
                SocialLoginType.FACEBOOK -> userRepository.deleteUserFacebook()
                SocialLoginType.KAKAO -> userRepository.deleteUserKakao()
                SocialLoginType.GOOGLE -> userRepository.deleteUserGoogle()
                else -> return@launch
            }
            result
                .onSuccess {
                    _uiState.update { it.copy(disconnectDialogType = SocialLoginType.NONE) }
                    refreshAfterConnect()
                }
                .onFailure { handleError(it) }
        }
    }

    // endregion

    private fun fetchSocialProviders() {
        viewModelScope.launch {
            userRepository.getSocialProviders()
                .onSuccess { providers ->
                    _uiState.update { it.copy(socialProviders = providers) }
                }
                .onFailure { handleError(it) }
        }
    }

    private suspend fun refreshAfterConnect() {
        userRepository.fetchUserInfo()
        fetchSocialProviders()
    }

    private suspend fun handleError(error: DomainError) {
        _uiEvent.emit(SocialLinkUiEvent.ShowToast(displayMessageResolver.getDisplayMessage(error)))
    }
}

data class SocialLinkUiState(
    val socialProviders: SocialProviders = SocialProviders(
        local = false, facebook = false, google = false, kakao = false, apple = false,
    ),
    val disconnectDialogType: SocialLoginType = SocialLoginType.NONE,
)

sealed interface SocialLinkUiEvent {
    data class ShowToast(val message: String) : SocialLinkUiEvent
    data object LaunchGoogleSignIn : SocialLinkUiEvent
    data object LaunchFacebookLogin : SocialLinkUiEvent
    data object LaunchKakaoLogin : SocialLinkUiEvent
}
