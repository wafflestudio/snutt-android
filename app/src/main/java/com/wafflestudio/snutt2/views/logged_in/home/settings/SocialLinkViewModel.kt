package com.wafflestudio.snutt2.views.logged_in.home.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthError
import com.kakao.sdk.common.model.AuthErrorCause
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.core.network.model.GetUserFacebookResults
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.lib.android.toast
import com.wafflestudio.snutt2.lib.network.dto.core.UserDto
import com.wafflestudio.snutt2.ui.state.SocialLoginState
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

    val socialLoginState = MutableStateFlow<SocialLoginState>(SocialLoginState.Initial)

    private val loginWithKakaoAccountCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                updateSocialLoginState(SocialLoginState.Cancelled)
            } else if (error is AuthError && error.reason == AuthErrorCause.AccessDenied) {
                updateSocialLoginState(SocialLoginState.Cancelled)
            } else {
                updateSocialLoginState(SocialLoginState.Failed)
            }
        } else if (token != null) {
            updateSocialLoginState(SocialLoginState.Success(token.accessToken))
        } else {
            updateSocialLoginState(SocialLoginState.Failed)
        }
    }

    suspend fun fetchUserInfo() {
        userRepository.fetchUserInfo()
    }

    suspend fun fetchUserFacebook(): GetUserFacebookResults {
        return userRepository.getUserFacebook()
    }

    suspend fun connectFacebook(id: String, token: String) {
        userRepository.postUserFacebook(id, token)
    }

    suspend fun disconnectFacebook() {
        userRepository.deleteUserFacebook()
    }

    fun updateSocialLoginState(state: SocialLoginState) {
        viewModelScope.launch {
            socialLoginState.emit(state)
        }
    }

    fun triggerKakaoSignin(
        context: Context,
    ) {
        updateSocialLoginState(SocialLoginState.InProgress)
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            UserApiClient.instance.loginWithKakaoTalk(context) { token, loginError ->
                if (loginError != null) {
                    if (loginError is ClientError && loginError.reason == ClientErrorCause.Cancelled) {
                        updateSocialLoginState(SocialLoginState.Cancelled)
                    } else if (loginError is AuthError && loginError.reason == AuthErrorCause.AccessDenied) {
                        updateSocialLoginState(SocialLoginState.Cancelled)
                    } else {
                        // 카카오계정으로 로그인
                        UserApiClient.instance.loginWithKakaoAccount(context = context, callback = loginWithKakaoAccountCallback)
                    }
                } else if (token != null) {
                    updateSocialLoginState(SocialLoginState.Success(token.accessToken))
                } else {
                    updateSocialLoginState(SocialLoginState.Failed)
                }
            }
        } else {
            // 카카오계정으로 로그인
            UserApiClient.instance.loginWithKakaoAccount(context = context, callback = loginWithKakaoAccountCallback)
        }
    }
}
