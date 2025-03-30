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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
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
import com.wafflestudio.snutt2.model.showDialog
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.views.LocalNavController
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.Margin
import kotlinx.coroutines.launch

@Composable
fun SocialLinkRoute(
    modifier: Modifier = Modifier,
    viewModel: SocialLinkViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val navController = LocalNavController.current
    val coroutineScope = rememberCoroutineScope()
    val activityContext = LocalContext.current as Activity

    val clientId = context.getString(R.string.web_client_id)
    val clientSecret = context.getString(R.string.web_client_secret)

    val socialLinkViewModel = hiltViewModel<SocialLinkViewModel>()
    val disconnectSocialDialogState by socialLinkViewModel.disconnectSocialDialogState.collectAsStateWithLifecycle()
    val socialLinkUiState by socialLinkViewModel.socialLinkUiState.collectAsStateWithLifecycle()

    val handleFacebookConnect = {
        coroutineScope.launch {
            val loginResult = facebookLogin(context, socialLinkViewModel::showToast)
            socialLinkViewModel.connectFacebook(loginResult.accessToken.token)
        }
        Unit
    }

    val loginWithKakaoAccountCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                socialLinkViewModel.showToast(context.getString(R.string.sign_in_kakao_failed_cancelled))
            } else if (error is AuthError && error.reason == AuthErrorCause.AccessDenied) {
                socialLinkViewModel.showToast(context.getString(R.string.sign_in_kakao_failed_cancelled))
            } else {
                socialLinkViewModel.showToast(context.getString(R.string.sign_in_kakao_failed_unknown))
            }
        } else if (token != null) {
            socialLinkViewModel.connectKakao(token.accessToken)
        } else {
            socialLinkViewModel.showToast(context.getString(R.string.sign_in_kakao_failed_unknown))
        }
    }

    val handleKakaoConnect: () -> Unit = {
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            UserApiClient.instance.loginWithKakaoTalk(context) { token, loginError ->
                if (loginError != null) {
                    if (loginError is ClientError && loginError.reason == ClientErrorCause.Cancelled) {
                        socialLinkViewModel.showToast(context.getString(R.string.sign_in_kakao_failed_cancelled))
                    } else if (loginError is AuthError && loginError.reason == AuthErrorCause.AccessDenied) {
                        socialLinkViewModel.showToast(context.getString(R.string.sign_in_kakao_failed_cancelled))
                    } else {
                        // 카카오계정으로 로그인
                        UserApiClient.instance.loginWithKakaoAccount(context = context, callback = loginWithKakaoAccountCallback)
                    }
                } else if (token != null) {
                    socialLinkViewModel.connectKakao(token.accessToken)
                } else {
                    socialLinkViewModel.showToast(context.getString(R.string.sign_in_kakao_failed_unknown))
                }
            }
        } else {
            // 카카오계정으로 로그인
            UserApiClient.instance.loginWithKakaoAccount(context = context, callback = loginWithKakaoAccountCallback)
        }
    }

    val handleGoogleSignInCallback: (String) -> Unit = { authCode: String ->
        coroutineScope.launch {
            val googleAccessToken = socialLinkViewModel.getAccessTokenByAuthCode(
                authCode = authCode,
                clientId = clientId,
                clientSecret = clientSecret,
            )
            if (googleAccessToken != null) {
                socialLinkViewModel.connectGoogle(googleAccessToken)
            } else {
                socialLinkViewModel.showToast(context.getString(R.string.sign_in_sign_in_google_failed_unknown))
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
                        socialLinkViewModel.showToast(context.getString(R.string.sign_in_sign_in_google_failed_unknown))
                        return@rememberLauncherForActivityResult
                    }
                    handleGoogleSignInCallback(authCode)
                } catch (e: ApiException) {
                    socialLinkViewModel.showToast(context.getString(R.string.sign_in_sign_in_google_failed_unknown))
                }
            }
            Activity.RESULT_CANCELED -> {
                socialLinkViewModel.showToast(context.getString(R.string.sign_in_sign_in_google_cancelled))
            }
            else -> {
                socialLinkViewModel.showToast(context.getString(R.string.sign_in_sign_in_google_failed_unknown))
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
        googleSignInClient.signOut().addOnCompleteListener {
            val signInIntent = googleSignInClient.signInIntent
            googleLoginActivityResultLauncher.launch(signInIntent)
        }
        Unit
    }

    val handleSocialDisconnect = { type: SocialLoginType ->
        coroutineScope.launch {
            LoginManager.getInstance().logOut()
            socialLinkViewModel.disconnectSocialLogin(type)
            socialLinkViewModel.changeDialogState(SocialLoginType.NONE)
        }
        Unit
    }

    LaunchedEffect(Unit) {
        socialLinkViewModel.toastState.collect { message ->
            if (message.isNotEmpty()) {
                context.toast(message)
            }
        }
    }

    SocialLinkScreen(
        modifier = modifier,
        onBackClick = { navController.popBackStack() },
        uiState = socialLinkUiState,
        dialogState = disconnectSocialDialogState,
        changeDialogState = viewModel::changeDialogState,
        handleFacebookConnect = handleFacebookConnect,
        handleKakaoConnect = handleKakaoConnect,
        handleGoogleConnect = handleGoogleConnect,
        handleSocialDisconnect = handleSocialDisconnect,
    )
}

@Composable
fun SocialLinkScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    uiState: SocialLinkUiState,
    dialogState: SocialLoginType,
    changeDialogState: (SocialLoginType) -> Unit,
    handleFacebookConnect: () -> Unit,
    handleKakaoConnect: () -> Unit,
    handleGoogleConnect: () -> Unit,
    handleSocialDisconnect: (SocialLoginType) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SNUTTColors.SettingBackground),
    ) {
        SimpleTopBar(
            title = stringResource(R.string.social_link_title),
            onClickNavigateBack = onBackClick,
        )

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState()),
        ) {
            Margin(height = 10.dp)

            SocialButtonItem(
                type = SocialLoginType.KAKAO,
                linkState = uiState.kakao,
                onConnectClick = handleKakaoConnect,
                onDisconnectClick = changeDialogState,
            )

            Margin(height = 10.dp)

            SocialButtonItem(
                type = SocialLoginType.GOOGLE,
                linkState = uiState.google,
                onConnectClick = handleGoogleConnect,
                onDisconnectClick = changeDialogState,
            )

            Margin(height = 10.dp)

            SocialButtonItem(
                type = SocialLoginType.FACEBOOK,
                linkState = uiState.facebook,
                onConnectClick = handleFacebookConnect,
                onDisconnectClick = changeDialogState,
            )
        }
    }

    if (dialogState.showDialog()) {
        CustomDialog(
            onDismiss = { changeDialogState(SocialLoginType.NONE) },
            onConfirm = { handleSocialDisconnect(dialogState) },
            title = stringResource(R.string.settings_user_config_social_disconnect_message, dialogState.getString()),
            content = {},
            positiveButtonText = stringResource(R.string.social_disconnect),
        )
    }
}

@Composable
fun SocialButtonItem(
    type: SocialLoginType,
    linkState: SocialLinkUiState.SocialProviders,
    onConnectClick: () -> Unit,
    onDisconnectClick: (SocialLoginType) -> Unit,
) {
    if (linkState.isLinked()) {
        SettingItem(
            title = stringResource(R.string.social_unlink, type.getString()),
            titleColor = colorResource(R.color.theme_snutt_0),
            hasNextPage = false,
            onClick = { onDisconnectClick(type) },
        )
    } else {
        SettingItem(
            title = stringResource(R.string.social_link, type.getString()),
            hasNextPage = false,
            onClick = onConnectClick,
        )
    }
}
