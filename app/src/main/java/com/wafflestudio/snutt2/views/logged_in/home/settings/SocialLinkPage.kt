package com.wafflestudio.snutt2.views.logged_in.home.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.facebook.login.LoginManager
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
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.views.LocalApiOnError
import com.wafflestudio.snutt2.views.LocalApiOnProgress
import com.wafflestudio.snutt2.views.LocalNavController
import com.wafflestudio.snutt2.views.NavigationDestination
import com.wafflestudio.snutt2.views.launchSuspendApi
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.Margin
import com.wafflestudio.snutt2.views.navigateAsOrigin
import kotlinx.coroutines.launch

@Composable
fun SocialLinkPage() {
    val context = LocalContext.current
    val navController = LocalNavController.current
    val coroutineScope = rememberCoroutineScope()
    val apiOnProgress = LocalApiOnProgress.current
    val apiOnError = LocalApiOnError.current

    val socialLinkViewModel = hiltViewModel<SocialLinkViewModel>()
    val socialProviders by socialLinkViewModel.socialProviders.collectAsStateWithLifecycle()

    var disconnectSocialDialogState by remember { mutableStateOf(SocialLoginType.NONE) }

    LaunchedEffect(Unit) {
        socialLinkViewModel.fetchSocialProviders()
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SNUTTColors.SettingBackground),
    ) {
        SimpleTopBar(
            title = stringResource(R.string.social_link_title),
            onClickNavigateBack = { navController.popBackStack() },
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
//
//            Margin(height = 10.dp)
//
//            SettingItem(
//                title = stringResource(R.string.social_link_google),
//                hasNextPage = false,
//                onClick = {},
//            )

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
            title = context.getString(R.string.settings_user_config_social_disconnect, disconnectSocialDialogState.getString()),
        ) {
            Text(text = context.getString(R.string.settings_user_config_social_disconnect_message, disconnectSocialDialogState.getString()), style = SNUTTTypography.body2)
        }
    }
}
