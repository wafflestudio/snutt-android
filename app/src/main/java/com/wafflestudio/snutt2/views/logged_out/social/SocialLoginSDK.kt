package com.wafflestudio.snutt2.views.logged_out.social

import android.app.Activity
import android.content.Context
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
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
import com.kakao.sdk.common.model.AuthError
import com.kakao.sdk.common.model.AuthErrorCause
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.ui.state.SocialLoginType

interface SocialLoginSDK {
    fun request(
        context: Context,
        onSuccess: ((String) -> Unit),
        onCancel: () -> Unit,
        onFail: () -> Unit,
    )

    companion object {
        fun getSDK(type: SocialLoginType) = when(type) {
            SocialLoginType.KAKAO -> KakaoLoginSDK()
            SocialLoginType.GOOGLE -> GoogleLoginSDK()
            SocialLoginType.FACEBOOK -> FacebookLoginSDK()
        }
    }
}

class FacebookLoginSDK : SocialLoginSDK {
    private val callbackManager = CallbackManager.Factory.create()
    private val facebookLoginManager = LoginManager.getInstance()

    override fun request(context: Context, onSuccess: (String) -> Unit, onCancel: () -> Unit, onFail: () -> Unit) {
        facebookLoginManager.registerCallback(callbackManager, object: FacebookCallback<LoginResult> {
            override fun onCancel() {
                onCancel()
            }

            override fun onError(error: FacebookException) {
                onFail()
            }

            override fun onSuccess(result: LoginResult) {
                onSuccess(result.accessToken.token)
            }
        })
        facebookLoginManager.logInWithReadPermissions(
            context as? ActivityResultRegistryOwner ?: return,
            callbackManager,
            emptyList(),
        )

    }
}

class GoogleLoginSDK : SocialLoginSDK {
    override fun request(context: Context, onSuccess: (String) -> Unit, onCancel: () -> Unit, onFail: () -> Unit) {
        val clientId = context.getString(R.string.web_client_id)

        val googleSignInOption = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestServerAuthCode(clientId)
            .build()
        val googleSignInClient = GoogleSignIn.getClient(context, googleSignInOption)

        googleSignInClient.signOut().addOnCompleteListener {
            val googleLoginActivityResultLauncher = (context as? AppCompatActivity)?.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                when (result.resultCode) {
                    Activity.RESULT_OK -> {
                        val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                        try {
                            val account = task.getResult(ApiException::class.java)
                            val authCode = account?.serverAuthCode
                            if (authCode == null) {
                                onFail()
                            }
                            onSuccess(authCode!!)
                        } catch (e: ApiException) {
                            onFail()
                        }
                    }

                    Activity.RESULT_CANCELED -> {
                        onCancel()
                    }

                    else -> {
                        onFail()
                    }
                }
            }
            googleLoginActivityResultLauncher?.launch(googleSignInClient.signInIntent) ?: run {
                onFail()
            }
        }
    }
}

class KakaoLoginSDK : SocialLoginSDK {
    override fun request(context: Context, onSuccess: (String) -> Unit, onCancel: () -> Unit, onFail: () -> Unit) {
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            UserApiClient.instance.loginWithKakaoTalk(context) { token, loginError ->
                if (loginError != null) {
                    if (loginError is ClientError && loginError.reason == ClientErrorCause.Cancelled) {
                        onCancel()
                    } else if (loginError is AuthError && loginError.reason == AuthErrorCause.AccessDenied) {
                        onCancel()
                    } else {
                        onFail()
                    }
                } else if (token == null) {
                    onFail()
                } else {
                    onSuccess(token.accessToken)
                }
            }
        } else {
            // 카카오계정으로 로그인
            UserApiClient.instance.loginWithKakaoAccount(
                context = context,
                callback = { token, error ->
                    if (error != null) {
                        if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                            onCancel()
                        } else if (error is AuthError && error.reason == AuthErrorCause.AccessDenied) {
                            onCancel()
                        } else {
                            onFail()
                        }
                    } else if (token != null) {
                        onSuccess(token.accessToken)
                    } else {
                        onFail()
                    }
                },
            )
        }
    }
}
