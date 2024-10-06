package com.wafflestudio.snutt2.views.logged_out

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.BorderButton
import com.wafflestudio.snutt2.components.compose.DividerWithText
import com.wafflestudio.snutt2.components.compose.SocialLoginButton
import com.wafflestudio.snutt2.lib.android.toast
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.ui.state.SocialLoginState
import com.wafflestudio.snutt2.views.*
import com.wafflestudio.snutt2.views.logged_in.home.HomeViewModel
import com.wafflestudio.snutt2.views.logged_in.home.settings.SocialLinkViewModel
import com.wafflestudio.snutt2.views.logged_in.home.settings.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun TutorialPage() {
    val navController = LocalNavController.current
    val coroutineScope = rememberCoroutineScope()
    val apiOnError = LocalApiOnError.current
    val apiOnProgress = LocalApiOnProgress.current
    val context = LocalContext.current
    val activityContext = LocalContext.current as Activity

    val userViewModel = hiltViewModel<UserViewModel>()
    val homeViewModel = hiltViewModel<HomeViewModel>()
    val socialLinkViewModel = hiltViewModel<SocialLinkViewModel>()

    val kakaoLoginState by socialLinkViewModel.kakaolLoginState.collectAsStateWithLifecycle()
    val googleLoginState by socialLinkViewModel.googleLoginState.collectAsStateWithLifecycle()
    val facebookLoginState by socialLinkViewModel.facebookLoginState.collectAsStateWithLifecycle()

    val clientId = context.getString(R.string.web_client_id)
    val clientSecret = context.getString(R.string.web_client_secret)

    val loginWithFacebookAccessToken: (String) -> Unit = { facebookAccessToken ->
        coroutineScope.launch {
            launchSuspendApi(
                apiOnProgress = apiOnProgress,
                apiOnError = apiOnError,
                loadingIndicatorTitle = context.getString(R.string.sign_in_sign_in_button),
            ) {
                userViewModel.loginFacebook(
                    facebookAccessToken,
                )
                homeViewModel.refreshData()
                navController.navigateAsOrigin(NavigationDestination.Home)
            }
        }
    }

    val loginWithGoogleAccessToken: (String) -> Unit = { googleAccessToken: String ->
        coroutineScope.launch {
            launchSuspendApi(
                apiOnProgress = apiOnProgress,
                apiOnError = apiOnError,
                loadingIndicatorTitle = context.getString(R.string.sign_in_sign_in_button),
            ) {
                userViewModel.loginGoogle(googleAccessToken)
                homeViewModel.refreshData()
                navController.navigateAsOrigin(NavigationDestination.Home)
            }
        }
    }

    val googleLoginActivityResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        socialLinkViewModel.handleGoogleLoginActivityResult(result, clientId, clientSecret)
    }

    val googleSignInClient: GoogleSignInClient = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .requestServerAuthCode(clientId)
        .build().let {
            GoogleSignIn.getClient(activityContext, it)
        }

    // 구글 계정 선택 창 띄움
    val startGoogleSignIn = {
        val signInIntent = googleSignInClient.signInIntent
        socialLinkViewModel.updateGoogleLoginState(SocialLoginState.InProgress)
        googleLoginActivityResultLauncher.launch(signInIntent)
    }

    val loginWithKaKaoAccessToken: (String) -> Unit = { kakaoAccessToken ->
        coroutineScope.launch {
            launchSuspendApi(
                apiOnProgress = apiOnProgress,
                apiOnError = apiOnError,
                loadingIndicatorTitle = context.getString(R.string.sign_in_sign_in_button),
            ) {
                if (kakaoAccessToken.isNotEmpty()) {
                    userViewModel.loginKakao(kakaoAccessToken)
                    homeViewModel.refreshData()
                    navController.navigateAsOrigin(NavigationDestination.Home)
                    socialLinkViewModel.updateKakaoLoginState(SocialLoginState.Initial)
                } else {
                    socialLinkViewModel.updateKakaoLoginState(SocialLoginState.Failed)
                }
            }
        }
    }

    LaunchedEffect(kakaoLoginState) {
        when (kakaoLoginState) {
            is SocialLoginState.Initial -> {}
            is SocialLoginState.InProgress -> {}
            is SocialLoginState.Cancelled -> {
                context.toast(context.getString(R.string.sign_in_kakao_failed_cancelled))
                socialLinkViewModel.updateKakaoLoginState(SocialLoginState.Initial)
            }
            is SocialLoginState.Failed -> {
                context.toast(context.getString(R.string.sign_in_kakao_failed_unknown))
                socialLinkViewModel.updateKakaoLoginState(SocialLoginState.Initial)
            }
            is SocialLoginState.Success -> {
                loginWithKaKaoAccessToken((kakaoLoginState as SocialLoginState.Success).token)
            }
        }
    }

    LaunchedEffect(googleLoginState) {
        when (googleLoginState) {
            is SocialLoginState.Initial -> {}
            is SocialLoginState.InProgress -> {}
            is SocialLoginState.Cancelled -> {
                context.toast(context.getString(R.string.sign_in_sign_in_google_cancelled))
                socialLinkViewModel.updateGoogleLoginState(SocialLoginState.Initial)
            }
            is SocialLoginState.Failed -> {
                context.toast(context.getString(R.string.sign_in_sign_in_google_failed_unknown))
                socialLinkViewModel.updateGoogleLoginState(SocialLoginState.Initial)
            }
            is SocialLoginState.Success -> {
                loginWithGoogleAccessToken((googleLoginState as SocialLoginState.Success).token)
            }
        }
    }

    LaunchedEffect(facebookLoginState) {
        when (facebookLoginState) {
            is SocialLoginState.Initial -> {}
            is SocialLoginState.InProgress -> {}
            is SocialLoginState.Cancelled -> {
                context.toast(context.getString(R.string.sign_in_facebook_failed_cancelled))
                socialLinkViewModel.updateFacebookLoginState(SocialLoginState.Initial)
            }
            is SocialLoginState.Failed -> {
                context.toast(context.getString(R.string.sign_in_facebook_failed_unknown))
                socialLinkViewModel.updateFacebookLoginState(SocialLoginState.Initial)
            }
            is SocialLoginState.Success -> {
                loginWithFacebookAccessToken((facebookLoginState as SocialLoginState.Success).token)
            }
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
                    onClick = { socialLinkViewModel.triggerKakaoSignin(context) },
                )

                SocialLoginButton(
                    painter = painterResource(id = R.drawable.google_login),
                    onClick = {
                        googleSignInClient.signOut().addOnCompleteListener {
                            startGoogleSignIn()
                        }
                    },
                )

                SocialLoginButton(
                    painter = painterResource(id = R.drawable.facebook_login),
                    onClick = {
                        socialLinkViewModel.prepareFacebookSignin()
                        socialLinkViewModel.loginManager.logInWithReadPermissions(
                            context as ActivityResultRegistryOwner,
                            socialLinkViewModel.callbackManager,
                            emptyList(),
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
