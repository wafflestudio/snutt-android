package com.wafflestudio.snutt2.views.logged_in.home.settings

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
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
import com.wafflestudio.snutt2.components.compose.CustomDialog
import com.wafflestudio.snutt2.components.compose.SimpleTopBar
import com.wafflestudio.snutt2.lib.android.toast
import com.wafflestudio.snutt2.lib.facebookLogin
import com.wafflestudio.snutt2.model.SocialLoginType
import com.wafflestudio.snutt2.model.getString
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.views.LocalApiOnError
import com.wafflestudio.snutt2.views.LocalApiOnProgress
import com.wafflestudio.snutt2.views.launchSuspendApi
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.Margin
import kotlinx.coroutines.launch

@Composable
fun SocialLinkPage(
    onNavigateBack: () -> Unit,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val apiOnProgress = LocalApiOnProgress.current
    val apiOnError = LocalApiOnError.current
    val activityContext = LocalContext.current as Activity

    val clientId = context.getString(R.string.web_client_id)
    val clientSecret = context.getString(R.string.web_client_secret)

    val socialLinkViewModel = hiltViewModel<SocialLinkViewModel>()
    val socialProviders by socialLinkViewModel.socialProviders.collectAsStateWithLifecycle()

    var disconnectSocialDialogState by remember { mutableStateOf(SocialLoginType.NONE) }

    LaunchedEffect(Unit) {
        launchSuspendApi(
            apiOnProgress = apiOnProgress,
            apiOnError = apiOnError,
        ) {
            socialLinkViewModel.fetchSocialProviders()
        }
    }

    val handleFacebookConnect = {
        coroutineScope.launch {
            launchSuspendApi(
                apiOnProgress = apiOnProgress,
                apiOnError = apiOnError,
                loadingIndicatorTitle = context.getString(R.string.sign_in_sign_in_button),
            ) {
                val loginResult = facebookLogin(context)
                socialLinkViewModel.connectFacebook(
                    loginResult.accessToken.token,
                )
                socialLinkViewModel.fetchUserInfo()
                socialLinkViewModel.fetchSocialProviders()
            }
        }
    }

    val connectWithKaKaoAccessToken: (String) -> Unit = { kakaoAccessToken ->
        coroutineScope.launch {
            launchSuspendApi(
                apiOnProgress = apiOnProgress,
                apiOnError = apiOnError,
                loadingIndicatorTitle = context.getString(R.string.sign_in_sign_in_button),
            ) {
                if (kakaoAccessToken.isNotEmpty()) {
                    socialLinkViewModel.connectKakao(
                        kakaoAccessToken,
                    )
                    socialLinkViewModel.fetchUserInfo()
                    socialLinkViewModel.fetchSocialProviders()
                } else {
                    context.toast(context.getString(R.string.sign_in_kakao_failed_unknown))
                }
            }
        }
    }

    val loginWithKakaoAccountCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                context.toast(context.getString(R.string.sign_in_kakao_failed_cancelled))
            } else if (error is AuthError && error.reason == AuthErrorCause.AccessDenied) {
                context.toast(context.getString(R.string.sign_in_kakao_failed_cancelled))
            } else {
                context.toast(context.getString(R.string.sign_in_kakao_failed_unknown))
            }
        } else if (token != null) {
            connectWithKaKaoAccessToken(token.accessToken)
        } else {
            context.toast(context.getString(R.string.sign_in_kakao_failed_unknown))
        }
    }

    val handleKakaoConnect: () -> Unit = {
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            UserApiClient.instance.loginWithKakaoTalk(context) { token, loginError ->
                if (loginError != null) {
                    if (loginError is ClientError && loginError.reason == ClientErrorCause.Cancelled) {
                        context.toast(context.getString(R.string.sign_in_kakao_failed_cancelled))
                    } else if (loginError is AuthError && loginError.reason == AuthErrorCause.AccessDenied) {
                        context.toast(context.getString(R.string.sign_in_kakao_failed_cancelled))
                    } else {
                        // 카카오계정으로 로그인
                        UserApiClient.instance.loginWithKakaoAccount(context = context, callback = loginWithKakaoAccountCallback)
                    }
                } else if (token != null) {
                    connectWithKaKaoAccessToken(token.accessToken)
                } else {
                    context.toast(context.getString(R.string.sign_in_kakao_failed_unknown))
                }
            }
        } else {
            // 카카오계정으로 로그인
            UserApiClient.instance.loginWithKakaoAccount(context = context, callback = loginWithKakaoAccountCallback)
        }
    }

    val handleGoogleSignInCallback: (String) -> Unit = { authCode: String ->
        coroutineScope.launch {
            launchSuspendApi(
                apiOnProgress = apiOnProgress,
                apiOnError = apiOnError,
                loadingIndicatorTitle = context.getString(R.string.sign_in_sign_in_button),
            ) {
                val googleAccessToken = socialLinkViewModel.getAccessTokenByAuthCode(
                    authCode = authCode,
                    clientId = clientId,
                    clientSecret = clientSecret,
                )
                if (googleAccessToken != null) {
                    socialLinkViewModel.connectGoogle(
                        googleAccessToken,
                    )
                    socialLinkViewModel.fetchUserInfo()
                    socialLinkViewModel.fetchSocialProviders()
                } else {
                    context.toast(context.getString(R.string.sign_in_sign_in_google_failed_unknown))
                }
            }
        }
    }

    val googleLoginActivityResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        when (result.resultCode) {
            Activity.RESULT_OK -> {
                val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    val authCode = account?.serverAuthCode
                    if (authCode == null) {
                        context.toast(context.getString(R.string.sign_in_sign_in_google_failed_unknown))
                        return@rememberLauncherForActivityResult
                    }
                    handleGoogleSignInCallback(authCode)
                } catch (e: ApiException) {
                    context.toast(context.getString(R.string.sign_in_sign_in_google_failed_unknown))
                }
            }

            Activity.RESULT_CANCELED -> {
                context.toast(context.getString(R.string.sign_in_sign_in_google_cancelled))
            }

            else -> {
                context.toast(context.getString(R.string.sign_in_sign_in_google_failed_unknown))
            }
        }
    }

    val googleSignInClient: GoogleSignInClient = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .requestServerAuthCode(clientId)
        .build().let {
            GoogleSignIn.getClient(activityContext, it)
        }

    // 구글 계정 선택 창 띄움
    val handleGoogleConnect = {
        val signInIntent = googleSignInClient.signInIntent
        googleLoginActivityResultLauncher.launch(signInIntent)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SNUTTColors.SettingBackground),
    ) {
        SimpleTopBar(
            title = stringResource(R.string.social_link_title),
            onClickNavigateBack = onNavigateBack,
        )

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState()),
        ) {
            Margin(height = 10.dp)

            if (socialProviders.kakao) {
                SettingItem(
                    title = stringResource(R.string.social_unlink_kakao),
                    titleColor = colorResource(R.color.theme_snutt_0),
                    hasNextPage = false,
                    onClick = { disconnectSocialDialogState = SocialLoginType.KAKAO },
                )
            } else {
                SettingItem(
                    title = stringResource(R.string.social_link_kakao),
                    hasNextPage = false,
                    onClick = { handleKakaoConnect() },
                )
            }

            Margin(height = 10.dp)

            if (socialProviders.google) {
                SettingItem(
                    title = stringResource(R.string.social_unlink_google),
                    titleColor = colorResource(R.color.theme_snutt_0),
                    hasNextPage = false,
                    onClick = { disconnectSocialDialogState = SocialLoginType.GOOGLE },
                )
            } else {
                SettingItem(
                    title = stringResource(R.string.social_link_google),
                    hasNextPage = false,
                    onClick = {
                        googleSignInClient.signOut().addOnCompleteListener {
                            handleGoogleConnect()
                        }
                    },
                )
            }

            Margin(height = 10.dp)

            if (socialProviders.facebook) {
                SettingItem(
                    title = stringResource(R.string.social_unlink_facebook),
                    titleColor = colorResource(R.color.theme_snutt_0),
                    hasNextPage = false,
                    onClick = { disconnectSocialDialogState = SocialLoginType.FACEBOOK },
                )
            } else {
                SettingItem(
                    title = stringResource(R.string.social_link_facebook),
                    hasNextPage = false,
                    onClick = { handleFacebookConnect() },
                )
            }
        }
    }

    if (disconnectSocialDialogState != SocialLoginType.NONE) {
        CustomDialog(
            onDismiss = { disconnectSocialDialogState = SocialLoginType.NONE },
            onConfirm = {
                coroutineScope.launch {
                    launchSuspendApi(apiOnProgress, apiOnError) {
                        LoginManager.getInstance().logOut()
                        socialLinkViewModel.disconnectSocialLogin(disconnectSocialDialogState)
                        socialLinkViewModel.fetchUserInfo()
                        socialLinkViewModel.fetchSocialProviders()
                        disconnectSocialDialogState = SocialLoginType.NONE
                    }
                }
            },
            title = context.getString(R.string.settings_user_config_social_disconnect_message, disconnectSocialDialogState.getString()),
            content = {},
            positiveButtonText = stringResource(R.string.social_disconnect),
        )
    }
}
