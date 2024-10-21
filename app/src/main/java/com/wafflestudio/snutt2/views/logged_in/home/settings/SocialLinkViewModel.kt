package com.wafflestudio.snutt2.views.logged_in.home.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.lib.network.dto.core.UserDto
import com.wafflestudio.snutt2.ui.state.SocialLoginProgress
import com.wafflestudio.snutt2.ui.state.SocialLoginType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SocialLinkViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    val userInfo: StateFlow<UserDto?> = userRepository.user

    val socialLoginProgress: MutableStateFlow<SocialLoginProgress> = MutableStateFlow(SocialLoginProgress.Initial(null))

    fun loginKakao(token: String) {
        viewModelScope.launch {
            userRepository.postLoginKakao(token)
        }
    }

    fun loginGoogle(authCode: String, clientId: String, clientSecret: String) {
        viewModelScope.launch {
            val googleAccessToken = getAccessTokenByAuthCode(
                authCode = authCode,
                clientId = clientId,
                clientSecret = clientSecret,
            )
            if (googleAccessToken == null) {
                updateSocialLoginState(SocialLoginProgress.Failed(SocialLoginType.GOOGLE))
                return@launch
            }
            userRepository.postLoginGoogle(googleAccessToken)
        }
    }

    fun loginFacebook(token: String) {
        viewModelScope.launch {
            userRepository.postLoginFacebook(token)
        }
    }

    suspend fun fetchUserInfo() {
        userRepository.fetchUserInfo()
    }

    private suspend fun getAccessTokenByAuthCode(authCode: String, clientId: String, clientSecret: String): String? {
        return userRepository.getAccessTokenByAuthCode(authCode = authCode, clientId = clientId, clientSecret = clientSecret)
    }

    suspend fun connectFacebook(token: String) {
        userRepository.postUserFacebook(token)
    }

    suspend fun disconnectFacebook() {
        userRepository.deleteUserFacebook()
    }

    suspend fun updateSocialLoginState(state: SocialLoginProgress) {
        socialLoginProgress.emit(state)
    }
}
