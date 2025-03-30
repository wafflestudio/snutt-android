package com.wafflestudio.snutt2.views.logged_in.home.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.lib.network.ApiOnError
import com.wafflestudio.snutt2.lib.network.dto.GetSocialProvidersResults
import com.wafflestudio.snutt2.model.SocialLoginType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SocialLinkViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val apiOnError: ApiOnError,
) : ViewModel() {
    private val _socialProviders: MutableStateFlow<GetSocialProvidersResults> = MutableStateFlow(GetSocialProvidersResults(false, false, false, false, false))
    val socialProviders = _socialProviders.asStateFlow()

    private val _disconnectSocialDialogState: MutableStateFlow<SocialLoginType> = MutableStateFlow(SocialLoginType.NONE)
    val disconnectSocialDialogState = _disconnectSocialDialogState.asStateFlow()

    private val _toastState: MutableSharedFlow<String> = MutableSharedFlow()
    val toastState = _toastState

    private val _socialLinkUiState: MutableStateFlow<SocialLinkUiState> = MutableStateFlow(SocialLinkUiState.Default)
    val socialLinkUiState = _socialLinkUiState.asStateFlow()

    init {
        viewModelScope.launch {
            runCatching {
                fetchSocialProvidersNew()
            }.onFailure { e ->
                apiOnError(e)
                _socialLinkUiState.emit(SocialLinkUiState.Default)
            }
        }
    }

    suspend fun fetchSocialProvidersNew() {
        runCatching {
            _socialLinkUiState.emit(userRepository.getSocialProviders().socialLinkUiState())
        }.onFailure(apiOnError)
    }

    suspend fun fetchUserInfo() {
        userRepository.fetchUserInfo()
    }

    suspend fun fetchSocialProviders() {
        _socialProviders.emit(userRepository.getSocialProviders())
    } // TODO: 삭제

    fun connectFacebook(token: String) {
        viewModelScope.launch {
            runCatching {
                userRepository.postUserFacebook(token)
                fetchUserInfo()
                fetchSocialProvidersNew()
            }.onFailure(apiOnError)
        }
    }

    fun connectKakao(token: String) {
        viewModelScope.launch {
            runCatching {
                userRepository.postUserKakao(token)
                fetchUserInfo()
                fetchSocialProvidersNew()
            }.onFailure(apiOnError)
        }
    }

    fun connectGoogle(token: String) {
        viewModelScope.launch {
            runCatching {
                userRepository.postUserGoogle(token)
                fetchUserInfo()
                fetchSocialProvidersNew()
            }.onFailure(apiOnError)
        }
    }

    suspend fun getAccessTokenByAuthCode(authCode: String, clientId: String, clientSecret: String): String? {
        return runCatching {
            userRepository.getAccessTokenByAuthCode(authCode = authCode, clientId = clientId, clientSecret = clientSecret)
        }.getOrNull()
    }

    suspend fun disconnectSocialLogin(type: SocialLoginType) {
        when (type) {
            SocialLoginType.FACEBOOK -> disconnectFacebook()
            SocialLoginType.KAKAO -> disconnectKakao()
            SocialLoginType.GOOGLE -> disconnectGoogle()
            else -> {}
        }
    }

    private suspend fun disconnectFacebook() {
        userRepository.deleteUserFacebook()
    }

    private suspend fun disconnectKakao() {
        userRepository.deleteUserKakao()
    }

    private suspend fun disconnectGoogle() {
        userRepository.deleteUserGoogle()
    }

    fun changeDialogState(type: SocialLoginType) {
        viewModelScope.launch {
            _disconnectSocialDialogState.emit(type)
        }
    }

    fun showToast(message: String) {
        viewModelScope.launch {
            _toastState.emit(message)
        }
    }
}
