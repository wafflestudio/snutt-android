package com.wafflestudio.snutt2.views.logged_in.home.settings

import androidx.lifecycle.ViewModel
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.lib.network.dto.GetSocialProvidersResults
import com.wafflestudio.snutt2.model.SocialLoginType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SocialLinkViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {
    private val _socialProviders: MutableStateFlow<GetSocialProvidersResults> = MutableStateFlow(GetSocialProvidersResults(false, false, false, false, false))
    val socialProviders = _socialProviders.asStateFlow()

    suspend fun fetchUserInfo() {
        userRepository.fetchUserInfo()
    }

    suspend fun fetchSocialProviders() {
        _socialProviders.emit(userRepository.getSocialProviders())
    }

    suspend fun connectFacebook(token: String) {
        userRepository.postUserFacebook(token)
    }

    suspend fun connectKakao(token: String) {
        userRepository.postUserKakao(token)
    }

    suspend fun connectGoogle(token: String) {
        userRepository.postUserGoogle(token)
    }

    suspend fun getAccessTokenByAuthCode(authCode: String, clientId: String, clientSecret: String): String? {
        return userRepository.getAccessTokenByAuthCode(authCode = authCode, clientId = clientId, clientSecret = clientSecret)
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
}
