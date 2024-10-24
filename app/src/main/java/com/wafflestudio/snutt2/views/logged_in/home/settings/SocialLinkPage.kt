package com.wafflestudio.snutt2.views.logged_in.home.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.facebook.login.LoginManager
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.CustomDialog
import com.wafflestudio.snutt2.components.compose.SimpleTopBar
import com.wafflestudio.snutt2.lib.facebookLogin
import com.wafflestudio.snutt2.lib.network.dto.core.UserDto
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
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

    val viewModel = hiltViewModel<SocialLinkViewModel>()
    val user: UserDto? by viewModel.userInfo.collectAsState()

    var disconnectFacebookDialogState by remember { mutableStateOf(false) }

    var facebookConnected by remember(user?.fbName) {
        mutableStateOf(
            user?.fbName.isNullOrEmpty().not(),
        )
    }

    val handleFacebookConnect = {
        scope.launch {
            launchSuspendApi(
                apiOnProgress = apiOnProgress,
                apiOnError = apiOnError,
                loadingIndicatorTitle = context.getString(R.string.sign_in_sign_in_button),
            ) {
                val loginResult = facebookLogin(context)
                viewModel.connectFacebook(
                    loginResult.accessToken.userId,
                    loginResult.accessToken.token,
                )
                facebookConnected = true
                viewModel.fetchUserInfo()
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

            if (facebookConnected) {
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
                    onClick = { handleFacebookConnect() },
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
                        viewModel.disconnectFacebook()
                        LoginManager.getInstance().logOut()
                        facebookConnected = false
                        disconnectFacebookDialogState = false
                        viewModel.fetchUserInfo()
                    }
                }
            },
            title = stringResource(R.string.settings_user_config_facebook_disconnect),
        ) {
            Text(text = stringResource(R.string.settings_user_config_disconnect_facebook_message), style = SNUTTTypography.body2)
        }
    }
}
