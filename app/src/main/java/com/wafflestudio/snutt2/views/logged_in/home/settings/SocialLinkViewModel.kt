package com.wafflestudio.snutt2.views.logged_in.home.settings

import android.app.Activity
import android.content.Context
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthError
import com.kakao.sdk.common.model.AuthErrorCause
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.lib.network.dto.core.UserDto
import com.wafflestudio.snutt2.ui.state.SocialLoginState
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
    val callbackManager = CallbackManager.Factory.create()
    val facebookLoginManager = LoginManager.getInstance()

    val userInfo: StateFlow<UserDto?> = userRepository.user

    val socialLoginProgress = MutableStateFlow<SocialLoginState>(SocialLoginState.Initial(null))

    private val loginWithKakaoAccountCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                updateSocialLoginState(SocialLoginState.Cancelled(SocialLoginType.KAKAO))
            } else if (error is AuthError && error.reason == AuthErrorCause.AccessDenied) {
                updateSocialLoginState(SocialLoginState.Cancelled(SocialLoginType.KAKAO))
            } else {
                updateSocialLoginState(SocialLoginState.Failed(SocialLoginType.KAKAO))
            }
        } else if (token != null) {
            updateSocialLoginState(SocialLoginState.Success(SocialLoginType.KAKAO, token.accessToken))
        } else {
            updateSocialLoginState(SocialLoginState.Failed(SocialLoginType.KAKAO))
        }
    }

    private val facebookTokenCallback = object : FacebookCallback<LoginResult> {
        override fun onSuccess(result: LoginResult) {
            updateSocialLoginState(SocialLoginState.Success(SocialLoginType.FACEBOOK, result.accessToken.token))
        }

        override fun onCancel() {
            updateSocialLoginState(SocialLoginState.Cancelled(SocialLoginType.FACEBOOK))
        }

        override fun onError(error: FacebookException) {
            updateSocialLoginState(SocialLoginState.Failed(SocialLoginType.FACEBOOK))
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

    fun updateSocialLoginState(state: SocialLoginState) {
        viewModelScope.launch {
            socialLoginProgress.emit(state)
        }
    }

    fun startSocialLogin(type: SocialLoginType, context: Context) {
        if (socialLoginProgress.value is SocialLoginState.InProgress) {
            return
        }
        updateSocialLoginState(SocialLoginState.InProgress(type))

        when (type) {
            SocialLoginType.GOOGLE -> {
                signInGoogle(context)
            }
            SocialLoginType.KAKAO -> {
                signInKakao(context)
            }

            SocialLoginType.FACEBOOK -> {
                signInFacebook(context)
            }
        }
    }

    private fun signInGoogle(context: Context) {
        val clientId = context.getString(R.string.web_client_id)
        val clientSecret = context.getString(R.string.web_client_secret)

        val googleSignInClient = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestServerAuthCode(clientId)
            .build().let {
                GoogleSignIn.getClient(context, it)
            }

        googleSignInClient.signOut().addOnCompleteListener {
            val googleLoginActivityResultLauncher = (context as? AppCompatActivity)?.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                handleGoogleLoginActivityResult(result, clientId, clientSecret)
            }
            googleLoginActivityResultLauncher?.launch(googleSignInClient.signInIntent) ?: run {
                updateSocialLoginState(SocialLoginState.Failed(SocialLoginType.GOOGLE))
            }
        }
    }

    private fun signInFacebook(context: Context) {
        facebookLoginManager.registerCallback(callbackManager, facebookTokenCallback)
        facebookLoginManager.logInWithReadPermissions(
            context as? ActivityResultRegistryOwner ?: return,
            callbackManager,
            emptyList(),
        )
    }

    private fun signInKakao(context: Context) {
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            UserApiClient.instance.loginWithKakaoTalk(context) { token, loginError ->
                if (loginError != null) {
                    if (loginError is ClientError && loginError.reason == ClientErrorCause.Cancelled) {
                        updateSocialLoginState(SocialLoginState.Cancelled(type = SocialLoginType.KAKAO))
                        return@loginWithKakaoTalk
                    }
                    if (loginError is AuthError && loginError.reason == AuthErrorCause.AccessDenied) {
                        updateSocialLoginState(SocialLoginState.Cancelled(type = SocialLoginType.KAKAO))
                        return@loginWithKakaoTalk
                    }
                    UserApiClient.instance.loginWithKakaoAccount(context = context, callback = loginWithKakaoAccountCallback)
                } else if (token != null) {
                    updateSocialLoginState(SocialLoginState.Success(token = token.accessToken, type = SocialLoginType.KAKAO))
                } else {
                    updateSocialLoginState(SocialLoginState.Failed(type = SocialLoginType.KAKAO))
                }
            }
        } else {
            // 카카오계정으로 로그인
            UserApiClient.instance.loginWithKakaoAccount(context = context, callback = loginWithKakaoAccountCallback)
        }
    }

    private fun handleGoogleLoginActivityResult(
        result: ActivityResult,
        clientId: String,
        clientSecret: String,
    ) {
        when (result.resultCode) {
            Activity.RESULT_OK -> {
                val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    val authCode = account?.serverAuthCode
                    if (authCode == null) {
                        updateSocialLoginState(SocialLoginState.Failed(SocialLoginType.GOOGLE))
                        return
                    }
                    viewModelScope.launch {
                        val googleAccessToken = getAccessTokenByAuthCode(
                            authCode = authCode,
                            clientId = clientId,
                            clientSecret = clientSecret,
                        )
                        if (googleAccessToken == null) {
                            updateSocialLoginState(SocialLoginState.Failed(SocialLoginType.GOOGLE))
                            return@launch
                        }
                        updateSocialLoginState(SocialLoginState.Success(SocialLoginType.GOOGLE, googleAccessToken))
                    }
                } catch (e: ApiException) {
                    updateSocialLoginState(SocialLoginState.Failed(SocialLoginType.GOOGLE))
                }
            }

            Activity.RESULT_CANCELED -> {
                updateSocialLoginState(SocialLoginState.Cancelled(SocialLoginType.GOOGLE))
            }

            else -> {
                updateSocialLoginState(SocialLoginState.Failed(SocialLoginType.GOOGLE))
            }
        }
    }
}
