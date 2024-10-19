package com.wafflestudio.snutt2.views.logged_out

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.BorderButton
import com.wafflestudio.snutt2.components.compose.DividerWithText
import com.wafflestudio.snutt2.components.compose.SocialLoginButton
import com.wafflestudio.snutt2.lib.android.toast
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.ui.state.SocialLoginState
import com.wafflestudio.snutt2.ui.state.SocialLoginType
import com.wafflestudio.snutt2.ui.state.getString
import com.wafflestudio.snutt2.views.LocalNavController
import com.wafflestudio.snutt2.views.NavigationDestination
import com.wafflestudio.snutt2.views.logged_in.home.settings.SocialLinkViewModel
import com.wafflestudio.snutt2.views.logged_out.social.SocialLoginSDK
import com.wafflestudio.snutt2.views.navigateAsOrigin

@Composable
fun TutorialPage() {
    val navController = LocalNavController.current
    val context = LocalContext.current
    val socialLinkViewModel = hiltViewModel<SocialLinkViewModel>()
    val socialLoginProgress by socialLinkViewModel.socialLoginProgress.collectAsStateWithLifecycle()

    LaunchedEffect(socialLoginProgress) {
        when (val state = socialLoginProgress) {
            SocialLoginState.Cancelled -> {
                context.toast(context.getString(R.string.social_signin_failed_cancelled, state.type.getString()))
            }

            SocialLoginState.Failed -> {
                context.toast(context.getString(R.string.social_signin_kakao_failed_unknown, state.type.getString()))
            }

            SocialLoginState.Success -> {
                navController.navigateAsOrigin(NavigationDestination.Home)
            }

            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
            .background(SNUTTColors.White900),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.weight(25f))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = stringResource(R.string.sign_in_logo_title),
                modifier = Modifier.padding(top = 20.dp, bottom = 15.dp),
            )

            Text(
                text = stringResource(R.string.sign_in_logo_title),
                style = SNUTTTypography.h1,
            )
        }

        Spacer(modifier = Modifier.weight(13f))

        Column(
            verticalArrangement = Arrangement.Center,
        ) {
            BorderButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                bgColor = colorResource(R.color.theme_snutt_5),
                color = colorResource(R.color.theme_snutt_5),
                cornerRadius = 6.dp,
                onClick = { navController.navigate(NavigationDestination.SignIn) },
            ) {
                Text(
                    text = stringResource(R.string.tutorial_sign_in_button),
                    style = SNUTTTypography.button,
                    color = SNUTTColors.White900,
                )
            }

            BorderButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                color = SNUTTColors.White900,
                cornerRadius = 10.dp,
                onClick = { navController.navigate(NavigationDestination.SignUp) },
            ) {
                Text(
                    text = stringResource(R.string.tutorial_sign_up_button),
                    style = SNUTTTypography.button,
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            DividerWithText(
                color = SNUTTColors.Gray200,
                textStyle = SNUTTTypography.subtitle2,
                text = stringResource(R.string.continue_with_sns_account),
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp, alignment = Alignment.CenterHorizontally),
            ) {
                SocialLoginButton(
                    painter = painterResource(id = R.drawable.kakao_login),
                    onClick = {
                        SocialLoginSDK.getSDK(SocialLoginType.KAKAO).request(context,
                            onSuccess = { token ->
                                socialLinkViewModel.loginKakao(token)
                            },
                            onFail = {
                                context.toast(context.getString(R.string.social_signin_kakao_failed_unknown, state.type.getString()))
                            },
                            onCancel = {
                                context.toast(context.getString(R.string.social_signin_failed_cancelled, state.type.getString()))
                            }
                        )
                    },
                )

                SocialLoginButton(
                    painter = painterResource(id = R.drawable.google_login),
                    onClick = {
                        SocialLoginSDK.getSDK(SocialLoginType.GOOGLE).request(context,
                            onSuccess = { token ->
                                socialLinkViewModel.loginGoogle(token, context.getString(R.string.web_client_id), context.getString(R.string.web_client_secret))
                            },
                            onFail = {
                                context.toast(context.getString(R.string.social_signin_kakao_failed_unknown, SocialLoginType.GOOGLE.getString()))
                            },
                            onCancel = {
                                context.toast(context.getString(R.string.social_signin_failed_cancelled, SocialLoginType.GOOGLE.getString()))
                            }
                        )
                    },
                )

                SocialLoginButton(
                    painter = painterResource(id = R.drawable.facebook_login),
                    onClick = {
                        SocialLoginSDK.getSDK(SocialLoginType.FACEBOOK).request(context,
                            onSuccess = { token ->
                                socialLinkViewModel.loginFacebook(token)
                            },
                            onFail = {
                                context.toast(context.getString(R.string.social_signin_kakao_failed_unknown, state.type.getString()))
                            },
                            onCancel = {
                                context.toast(context.getString(R.string.social_signin_failed_cancelled, state.type.getString()))
                            }
                        )
                    },
                )
            }
        }

        Spacer(modifier = Modifier.weight(11f))
    }
}

@Preview(showBackground = true)
@Composable
fun TutorialPagePreview() {
    TutorialPage()
}
