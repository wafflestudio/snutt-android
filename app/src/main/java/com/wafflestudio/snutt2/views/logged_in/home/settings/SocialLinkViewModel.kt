package com.wafflestudio.snutt2.views.logged_in.home.settings

import android.app.Activity
import android.content.Context
import androidx.activity.result.ActivityResult
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthError
import com.kakao.sdk.common.model.AuthErrorCause
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.wafflestudio.snutt2.core.network.model.GetUserFacebookResults
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.lib.network.dto.core.SocialProvidersCheckDto
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
    val socialProviders = MutableStateFlow(
        SocialProvidersCheckDto(
            local = false,
            facebook = false,
            google = false,
            kakao = false,
            apple = false,
        )
    )

    val kakaolLoginState = MutableStateFlow<SocialLoginState>(SocialLoginState.Initial)
    val googleLoginState = MutableStateFlow<SocialLoginState>(SocialLoginState.Initial)

    private val loginWithKakaoAccountCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                updateKakaoLoginState(SocialLoginState.Cancelled)
            } else if (error is AuthError && error.reason == AuthErrorCause.AccessDenied) {
                updateKakaoLoginState(SocialLoginState.Cancelled)
            } else {
                updateKakaoLoginState(SocialLoginState.Failed)
            }
        } else if (token != null) {
            updateKakaoLoginState(SocialLoginState.Success(token.accessToken))
        } else {
            updateKakaoLoginState(SocialLoginState.Failed)
        }
    }

    suspend fun getSocialProviders() {
        viewModelScope.launch {
            socialProviders.emit(userRepository.getSocialProviders())
        }
    }

    suspend fun fetchUserInfo() {
        userRepository.fetchUserInfo()
    }

    suspend fun fetchUserFacebook(): GetUserFacebookResults {
        return userRepository.getUserFacebook()
    }

    suspend fun connectFacebook(token: String) {
        userRepository.postUserFacebook(token)
    }

    suspend fun disconnectFacebook() {
        userRepository.deleteUserFacebook()
    }

    fun updateKakaoLoginState(state: SocialLoginState) {
        viewModelScope.launch {
            kakaolLoginState.emit(state)
        }
    }

    fun updateGoogleLoginState(state: SocialLoginState) {
        viewModelScope.launch {
            googleLoginState.emit(state)
        }
    }

    fun triggerKakaoSignin(context: Context) {
        updateKakaoLoginState(SocialLoginState.InProgress)
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            UserApiClient.instance.loginWithKakaoTalk(context) { token, loginError ->
                if (loginError != null) {
                    if (loginError is ClientError && loginError.reason == ClientErrorCause.Cancelled) {
                        updateKakaoLoginState(SocialLoginState.Cancelled)
                    } else if (loginError is AuthError && loginError.reason == AuthErrorCause.AccessDenied) {
                        updateKakaoLoginState(SocialLoginState.Cancelled)
                    } else {
                        // 카카오계정으로 로그인
                        UserApiClient.instance.loginWithKakaoAccount(context = context, callback = loginWithKakaoAccountCallback)
                    }
                } else if (token != null) {
                    updateKakaoLoginState(SocialLoginState.Success(token.accessToken))
                } else {
                    updateKakaoLoginState(SocialLoginState.Failed)
                }
            }
        } else {
            // 카카오계정으로 로그인
            UserApiClient.instance.loginWithKakaoAccount(context = context, callback = loginWithKakaoAccountCallback)
        }
    }

    fun handleGoogleLoginActivityResult(result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val authCode = account?.serverAuthCode
                if (authCode != null) {
                    updateGoogleLoginState(SocialLoginState.Success(authCode))
                } else {
                    updateGoogleLoginState(SocialLoginState.Failed)
                }
            } catch (e: ApiException) {
                updateGoogleLoginState(SocialLoginState.Failed)
            }
        } else if (result.resultCode == Activity.RESULT_CANCELED) {
            updateGoogleLoginState(SocialLoginState.Cancelled)
        } else {
            updateGoogleLoginState(SocialLoginState.Failed)
        }
    }
}
