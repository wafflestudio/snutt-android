package com.wafflestudio.snutt2.feature.login

import android.annotation.SuppressLint
import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.auth.api.identity.AuthorizationRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.Scope
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthError
import com.kakao.sdk.common.model.AuthErrorCause
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.ui.components.compose.BorderButton
import com.wafflestudio.snutt2.ui.components.compose.DividerWithText
import com.wafflestudio.snutt2.ui.components.compose.SocialLoginButton
import com.wafflestudio.snutt2.ui.components.compose.clicks
import com.wafflestudio.snutt2.lib.android.toast
import com.wafflestudio.snutt2.lib.facebook.facebookLogin
import com.wafflestudio.snutt2.logging.AnalyticsScreen
import com.wafflestudio.snutt2.logging.logImpression
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography
import kotlinx.coroutines.launch

@SuppressLint("LocalContextGetResourceValueCall")
@Composable
fun TutorialPage(
    viewModel: TutorialViewModel = hiltViewModel(),
    onNavigateHome: () -> Unit,
    onNavigateSignIn: () -> Unit,
    onNavigateSignUp: () -> Unit,
    onNavigateAppReport: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val activityContext = context as Activity
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val clientId = context.getString(R.string.web_client_id)
    val clientSecret = context.getString(R.string.web_client_secret)

    val googleAuthLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            try {
                val authorizationResult = Identity.getAuthorizationClient(activityContext)
                    .getAuthorizationResultFromIntent(result.data!!)
                val authCode = authorizationResult.serverAuthCode
                if (authCode != null) {
                    viewModel.onGoogleAuthCodeReceived(authCode, clientId, clientSecret)
                } else {
                    context.toast(context.getString(R.string.sign_in_sign_in_google_failed_unknown))
                }
            } catch (e: Exception) {
                context.toast(context.getString(R.string.sign_in_sign_in_google_failed_unknown))
            }
        } else {
            context.toast(context.getString(R.string.sign_in_sign_in_google_cancelled))
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is TutorialUiEvent.ShowToast -> context.toast(event.message)
                is TutorialUiEvent.NavigateHome -> onNavigateHome()

                is TutorialUiEvent.LaunchGoogleSignIn -> {
                    val authorizationRequest = AuthorizationRequest.builder()
                        .setRequestedScopes(listOf(Scope("email")))
                        .requestOfflineAccess(clientId)
                        .build()
                    Identity.getAuthorizationClient(activityContext)
                        .authorize(authorizationRequest)
                        .addOnSuccessListener { authorizationResult ->
                            if (authorizationResult.hasResolution()) {
                                googleAuthLauncher.launch(
                                    IntentSenderRequest.Builder(authorizationResult.pendingIntent!!.intentSender).build(),
                                )
                            } else {
                                val authCode = authorizationResult.serverAuthCode
                                if (authCode != null) {
                                    viewModel.onGoogleAuthCodeReceived(authCode, clientId, clientSecret)
                                } else {
                                    context.toast(context.getString(R.string.sign_in_sign_in_google_failed_unknown))
                                }
                            }
                        }
                        .addOnFailureListener {
                            context.toast(context.getString(R.string.sign_in_sign_in_google_failed_unknown))
                        }
                }

                is TutorialUiEvent.LaunchFacebookLogin -> {
                    coroutineScope.launch {
                        try {
                            val loginResult = facebookLogin(context)
                            viewModel.onFacebookTokenReceived(loginResult.accessToken.token)
                        } catch (_: Exception) {
                            // facebookLogin 내부에서 취소/에러 toast 처리됨
                        }
                    }
                }

                is TutorialUiEvent.LaunchKakaoLogin -> {
                    val onKakaoToken: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                        if (error != null) {
                            if (error is ClientError && error.reason == ClientErrorCause.Cancelled ||
                                error is AuthError && error.reason == AuthErrorCause.AccessDenied
                            ) {
                                context.toast(context.getString(R.string.sign_in_kakao_failed_cancelled))
                            } else {
                                context.toast(context.getString(R.string.sign_in_kakao_failed_unknown))
                            }
                        } else if (token != null) {
                            viewModel.onKakaoTokenReceived(token.accessToken)
                        } else {
                            context.toast(context.getString(R.string.sign_in_kakao_failed_unknown))
                        }
                    }

                    if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
                        UserApiClient.instance.loginWithKakaoTalk(context) { token, loginError ->
                            if (loginError != null) {
                                if (loginError is ClientError && loginError.reason == ClientErrorCause.Cancelled ||
                                    loginError is AuthError && loginError.reason == AuthErrorCause.AccessDenied
                                ) {
                                    context.toast(context.getString(R.string.sign_in_kakao_failed_cancelled))
                                } else {
                                    UserApiClient.instance.loginWithKakaoAccount(context = context, callback = onKakaoToken)
                                }
                            } else if (token != null) {
                                viewModel.onKakaoTokenReceived(token.accessToken)
                            } else {
                                context.toast(context.getString(R.string.sign_in_kakao_failed_unknown))
                            }
                        }
                    } else {
                        UserApiClient.instance.loginWithKakaoAccount(context = context, callback = onKakaoToken)
                    }
                }
            }
        }
    }

    TutorialScreen(
        isLoading = uiState.isLoading,
        onNavigateSignIn = onNavigateSignIn,
        onNavigateSignUp = onNavigateSignUp,
        onNavigateAppReport = onNavigateAppReport,
        onFacebookSignIn = { viewModel.onFacebookLoginRequested() },
        onGoogleSignIn = { viewModel.onGoogleLoginRequested() },
        onKakaoSignIn = { viewModel.onKakaoLoginRequested() },
    )
}

@Composable
private fun TutorialScreen(
    isLoading: Boolean,
    onNavigateSignIn: () -> Unit,
    onNavigateSignUp: () -> Unit,
    onNavigateAppReport: () -> Unit,
    onFacebookSignIn: () -> Unit,
    onGoogleSignIn: () -> Unit,
    onKakaoSignIn: () -> Unit,
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .logImpression(AnalyticsScreen.Onboard),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(this@BoxWithConstraints.maxHeight * 0.3f))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = stringResource(R.string.sign_in_logo_title),
                )
                Text(
                    text = stringResource(R.string.sign_in_logo_title),
                    style = SNUTTTypography.h1,
                )
            }

            Spacer(modifier = Modifier.height(this@BoxWithConstraints.maxHeight * 0.16f))

            BorderButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                bgColor = colorResource(R.color.theme_snutt_5),
                color = colorResource(R.color.theme_snutt_5),
                cornerRadius = 6.dp,
                onClick = onNavigateSignIn,
            ) {
                Text(
                    text = stringResource(R.string.tutorial_sign_in_button),
                    style = SNUTTTypography.button,
                    color = SNUTTColors.AllWhite,
                )
            }

            Text(
                text = stringResource(R.string.tutorial_sign_up_button),
                style = SNUTTTypography.button,
                modifier = Modifier
                    .padding(top = 14.dp)
                    .clicks { onNavigateSignUp() },
            )

            Spacer(modifier = Modifier.weight(1f))

            DividerWithText(
                color = SNUTTColors.VacancyGray,
                textStyle = SNUTTTypography.subtitle2,
                text = stringResource(R.string.continue_with_sns_account),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SocialLoginButton(
                    painter = painterResource(id = R.drawable.kakao_login),
                    onClick = onKakaoSignIn,
                )
                SocialLoginButton(
                    painter = painterResource(id = R.drawable.google_login),
                    onClick = onGoogleSignIn,
                )
                SocialLoginButton(
                    painter = painterResource(id = R.drawable.facebook_login),
                    onClick = onFacebookSignIn,
                )
            }

            Spacer(modifier = Modifier.height(this@BoxWithConstraints.maxHeight * 0.06f))

            Text(
                color = SNUTTColors.Gray200,
                style = SNUTTTypography.subtitle2,
                text = stringResource(R.string.tutorial_help_button),
                modifier = Modifier.clicks { onNavigateAppReport() },
            )

            Spacer(modifier = Modifier.height(this@BoxWithConstraints.maxHeight * 0.05f))
        }

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SNUTTColors.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = SNUTTColors.AllWhite)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TutorialScreenPreview() {
    TutorialScreen(
        isLoading = false,
        onNavigateSignIn = {},
        onNavigateSignUp = {},
        onNavigateAppReport = {},
        onFacebookSignIn = {},
        onGoogleSignIn = {},
        onKakaoSignIn = {},
    )
}
