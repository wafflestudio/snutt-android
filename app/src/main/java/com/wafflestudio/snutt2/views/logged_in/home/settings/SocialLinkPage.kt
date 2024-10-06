package com.wafflestudio.snutt2.views.logged_in.home.settings

import androidx.activity.result.ActivityResultRegistryOwner
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
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.CustomDialog
import com.wafflestudio.snutt2.components.compose.SimpleTopBar
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.ui.state.SocialLoginState
import com.wafflestudio.snutt2.views.LocalApiOnError
import com.wafflestudio.snutt2.views.LocalApiOnProgress
import com.wafflestudio.snutt2.views.LocalNavController
import com.wafflestudio.snutt2.views.launchSuspendApi
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.Margin
import kotlinx.coroutines.launch

@Composable
fun SocialLinkPage() {
    val context = LocalContext.current
    val navController = LocalNavController.current
    val scope = rememberCoroutineScope()
    val apiOnProgress = LocalApiOnProgress.current
    val apiOnError = LocalApiOnError.current

    val socialLinkViewModel = hiltViewModel<SocialLinkViewModel>()
    val socialProviders by socialLinkViewModel.socialProviders.collectAsStateWithLifecycle()

    var disconnectFacebookDialogState by remember { mutableStateOf(false) }

    val facebookLoginState by socialLinkViewModel.facebookLoginState.collectAsStateWithLifecycle()

    val connectWithFacebookAccessToken: (String) -> Unit = { facebookAccessToken ->
        scope.launch {
            launchSuspendApi(
                apiOnProgress = apiOnProgress,
                apiOnError = apiOnError,
                loadingIndicatorTitle = context.getString(R.string.sign_in_sign_in_button),
            ) {
                socialLinkViewModel.connectFacebook(
                    facebookAccessToken,
                )
                socialLinkViewModel.getSocialProviders()
                socialLinkViewModel.fetchUserInfo()
            }
        }
    }

    LaunchedEffect(Unit) {
        socialLinkViewModel.getSocialProviders()
    }

    LaunchedEffect(facebookLoginState) {
        when (facebookLoginState) {
            is SocialLoginState.Initial -> {}
            is SocialLoginState.InProgress -> {}
            is SocialLoginState.Cancelled -> {
                //context.toast(context.getString(R.string.sign_in_facebook_failed_cancelled))
                socialLinkViewModel.updateFacebookLoginState(SocialLoginState.Initial)
            }
            is SocialLoginState.Failed -> {
                //context.toast(context.getString(R.string.sign_in_facebook_failed_unknown))
                socialLinkViewModel.updateFacebookLoginState(SocialLoginState.Initial)
            }
            is SocialLoginState.Success -> {
                connectWithFacebookAccessToken((facebookLoginState as SocialLoginState.Success).token)
            }
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
//            Margin(height = 10.dp)
//
//            SettingItem(
//                title = stringResource(R.string.social_link_kakao),
//                hasNextPage = false,
//                onClick = {},
//            )
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
                    onClick = { disconnectFacebookDialogState = true },
                )
            } else {
                SettingItem(
                    title = stringResource(R.string.social_link_facebook),
                    hasNextPage = false,
                    onClick = {
                        socialLinkViewModel.prepareFacebookSignIn()
                        socialLinkViewModel.loginManager.logInWithReadPermissions(
                            context as ActivityResultRegistryOwner,
                            socialLinkViewModel.callbackManager,
                            emptyList(),
                        )
                    },
                )
            }
        }
    }

    if (disconnectFacebookDialogState) {
        CustomDialog(
            onDismiss = { disconnectFacebookDialogState = false },
            onConfirm = {
                scope.launch {
                    launchSuspendApi(apiOnProgress, apiOnError) {
                        socialLinkViewModel.disconnectFacebook()
                        LoginManager.getInstance().logOut()
                        socialLinkViewModel.getSocialProviders()
                        disconnectFacebookDialogState = false
                        socialLinkViewModel.fetchUserInfo()
                    }
                }
            },
            title = stringResource(R.string.settings_user_config_facebook_disconnect),
        ) {
            Text(text = stringResource(R.string.settings_user_config_disconnect_facebook_message), style = SNUTTTypography.body2)
        }
    }
}
