package com.wafflestudio.snutt2.views.logged_out

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.BorderButton
import com.wafflestudio.snutt2.lib.android.toast
import com.wafflestudio.snutt2.lib.facebookLogin
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.views.*
import com.wafflestudio.snutt2.views.logged_in.home.HomeViewModel
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

    val clientId = context.getString(R.string.web_client_id)
    val clientSecret = context.getString(R.string.web_client_secret)

    val handleFacebookSignIn = {
        coroutineScope.launch {
            launchSuspendApi(
                apiOnProgress = apiOnProgress,
                apiOnError = apiOnError,
                loadingIndicatorTitle = context.getString(R.string.sign_in_sign_in_button),
            ) {
                val loginResult = facebookLogin(context)
                userViewModel.loginFacebook(
                    loginResult.accessToken.userId,
                    loginResult.accessToken.token,
                )
                homeViewModel.refreshData()
                navController.navigateAsOrigin(NavigationDestination.Home)
            }
        }
    }

    // accessToken을 SNUTT 서버에 보내 로그인을 실행한다.
    val loginGoogleByAccessToken: (String) -> Unit = { googleAccessToken: String ->
        coroutineScope.launch {
            launchSuspendApi(
                apiOnProgress = apiOnProgress,
                apiOnError = apiOnError,
                loadingIndicatorTitle = context.getString(R.string.sign_in_sign_in_button),
            ) {
                userViewModel.loginGoogle(
                    googleAccessToken,
                )
                homeViewModel.refreshData()
                navController.navigateAsOrigin(NavigationDestination.Home)
            }
        }
    }

    // 계정을 선택하면 authCode가 받아지는데, 그 authCode를 구글 API로 보내 accessToken을 얻는다.
    val getAccessTokenByAuthCode: (String) -> Unit = { authCode: String ->
        coroutineScope.launch {
            val response = userViewModel.getAccessTokenByAuthCode(
                authCode = authCode,
                clientId = clientId,
                clientSecret = clientSecret,
            )
            if (response.accessToken == null) {
                context.toast(context.getString(R.string.sign_in_sign_in_google_failed_unknown))
            } else {
                loginGoogleByAccessToken(response.accessToken)
            }
        }
    }

    // 계정 선택 activity에서 결과를 받는 부분
    val googleLoginActivityResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val authCode = account?.serverAuthCode
                if (authCode != null) {
                    getAccessTokenByAuthCode(authCode)
                } else {
                    context.toast(context.getString(R.string.sign_in_sign_in_google_failed_unknown))
                }
            } catch (e: ApiException) {
                context.toast(context.getString(R.string.sign_in_sign_in_google_failed_unknown))
            }
        } else if (result.resultCode == Activity.RESULT_CANCELED) {
            context.toast(context.getString(R.string.sign_in_sign_in_google_cancelled))
        }
    }

    val googleSignInClient: GoogleSignInClient = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .requestServerAuthCode(clientId)
        .build().let {
            GoogleSignIn.getClient(activityContext, it)
        }

    // 사용자가 버튼을 누르면 이게 실행되어 googleLoginActivityResultLauncher를 실행하게 된다.
    val handleGoogleSignIn = {
        val signInIntent = googleSignInClient.signInIntent
        googleLoginActivityResultLauncher.launch(signInIntent)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .background(SNUTTColors.White900),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier
                .weight(1f),
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

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            BorderButton(
                modifier = Modifier
                    .fillMaxWidth(),
                color = SNUTTColors.Gray200,
                cornerRadius = 10.dp,
                onClick = { navController.navigate(NavigationDestination.SignIn) },
            ) {
                Text(
                    text = stringResource(R.string.tutorial_sign_in_button),
                    style = SNUTTTypography.button,
                )
            }

            BorderButton(
                modifier = Modifier
                    .fillMaxWidth(),
                color = SNUTTColors.Gray200,
                cornerRadius = 10.dp,
                onClick = { navController.navigate(NavigationDestination.SignUp) },
            ) {
                Text(
                    text = stringResource(R.string.tutorial_sign_up_button),
                    style = SNUTTTypography.button,
                )
            }

            BorderButton(
                modifier = Modifier.fillMaxWidth(),
                color = SNUTTColors.FacebookBlue,
                cornerRadius = 10.dp,
                onClick = { handleFacebookSignIn() },
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.iconfacebook),
                        contentDescription = stringResource(id = R.string.sign_in_sign_in_facebook_button),
                        modifier = Modifier
                            .height(18.dp)
                            .padding(end = 12.dp),
                    )

                    Text(
                        text = stringResource(R.string.sign_in_sign_in_facebook_button),
                        color = SNUTTColors.FacebookBlue,
                        style = SNUTTTypography.button,
                    )
                }
            }

            BorderButton(
                modifier = Modifier.fillMaxWidth(),
                color = SNUTTColors.FacebookBlue,
                cornerRadius = 10.dp,
                onClick = {
                    googleSignInClient.signOut().addOnCompleteListener {
                        handleGoogleSignIn()
                    } 
                },
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.iconfacebook),
                        contentDescription = stringResource(id = R.string.sign_in_sign_in_google_button),
                        modifier = Modifier
                            .height(18.dp)
                            .padding(end = 12.dp),
                    )

                    Text(
                        text = stringResource(R.string.sign_in_sign_in_google_button),
                        color = SNUTTColors.FacebookBlue,
                        style = SNUTTTypography.button,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TutorialPagePreview() {
    TutorialPage()
}
