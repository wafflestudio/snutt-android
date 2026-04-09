package com.wafflestudio.snutt2.views.logged_in.home.settings

import android.annotation.SuppressLint
import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import com.wafflestudio.snutt2.ui.components.compose.CustomDialog
import com.wafflestudio.snutt2.ui.components.compose.SimpleTopBar
import com.wafflestudio.snutt2.lib.android.toast
import com.wafflestudio.snutt2.domain.model.SocialProviders
import com.wafflestudio.snutt2.lib.facebookLogin
import com.wafflestudio.snutt2.domain.model.SocialLoginType
import com.wafflestudio.snutt2.domain.model.getString
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import kotlinx.coroutines.launch

@SuppressLint("LocalContextGetResourceValueCall")
@Composable
fun SocialLinkPage(
    viewModel: SocialLinkViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
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

    // endregion

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is SocialLinkUiEvent.ShowToast -> context.toast(event.message)

                is SocialLinkUiEvent.LaunchGoogleSignIn -> {
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

                is SocialLinkUiEvent.LaunchFacebookLogin -> {
                    coroutineScope.launch {
                        try {
                            val loginResult = facebookLogin(context)
                            viewModel.onFacebookTokenReceived(loginResult.accessToken.token)
                        } catch (_: Exception) {
                        }
                    }
                }

                is SocialLinkUiEvent.LaunchKakaoLogin -> {
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

    SocialLinkScreen(
        uiState = uiState,
        onKakaoConnect = viewModel::onKakaoConnectRequested,
        onGoogleConnect = viewModel::onGoogleConnectRequested,
        onFacebookConnect = viewModel::onFacebookConnectRequested,
        onShowDisconnectDialog = viewModel::showDisconnectDialog,
        onDismissDisconnectDialog = viewModel::dismissDisconnectDialog,
        onConfirmDisconnect = viewModel::confirmDisconnect,
        onNavigateBack = onNavigateBack,
    )
}

@Composable
private fun SocialLinkScreen(
    uiState: SocialLinkUiState,
    onKakaoConnect: () -> Unit,
    onGoogleConnect: () -> Unit,
    onFacebookConnect: () -> Unit,
    onShowDisconnectDialog: (SocialLoginType) -> Unit,
    onDismissDisconnectDialog: () -> Unit,
    onConfirmDisconnect: () -> Unit,
    onNavigateBack: () -> Unit,
) {
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
            modifier = Modifier.verticalScroll(rememberScrollState()),
        ) {
            Spacer(Modifier.height(10.dp))

            if (uiState.socialProviders.kakao) {
                SettingItem(
                    title = stringResource(R.string.social_unlink_kakao),
                    titleColor = colorResource(R.color.theme_snutt_0),
                    hasNextPage = false,
                    onClick = { onShowDisconnectDialog(SocialLoginType.KAKAO) },
                )
            } else {
                SettingItem(
                    title = stringResource(R.string.social_link_kakao),
                    hasNextPage = false,
                    onClick = onKakaoConnect,
                )
            }

            Spacer(Modifier.height(10.dp))

            if (uiState.socialProviders.google) {
                SettingItem(
                    title = stringResource(R.string.social_unlink_google),
                    titleColor = colorResource(R.color.theme_snutt_0),
                    hasNextPage = false,
                    onClick = { onShowDisconnectDialog(SocialLoginType.GOOGLE) },
                )
            } else {
                SettingItem(
                    title = stringResource(R.string.social_link_google),
                    hasNextPage = false,
                    onClick = onGoogleConnect,
                )
            }

            Spacer(Modifier.height(10.dp))

            if (uiState.socialProviders.facebook) {
                SettingItem(
                    title = stringResource(R.string.social_unlink_facebook),
                    titleColor = colorResource(R.color.theme_snutt_0),
                    hasNextPage = false,
                    onClick = { onShowDisconnectDialog(SocialLoginType.FACEBOOK) },
                )
            } else {
                SettingItem(
                    title = stringResource(R.string.social_link_facebook),
                    hasNextPage = false,
                    onClick = onFacebookConnect,
                )
            }
        }
    }

    if (uiState.disconnectDialogType != SocialLoginType.NONE) {
        CustomDialog(
            onDismiss = onDismissDisconnectDialog,
            onConfirm = onConfirmDisconnect,
            title = stringResource(R.string.settings_user_config_social_disconnect_message, uiState.disconnectDialogType.getString()),
            content = {},
            positiveButtonText = stringResource(R.string.social_disconnect),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SocialLinkScreenPreview() {
    SocialLinkScreen(
        uiState = SocialLinkUiState(
            socialProviders = SocialProviders(
                local = true, facebook = false, google = true, kakao = false, apple = false,
            ),
        ),
        onKakaoConnect = {},
        onGoogleConnect = {},
        onFacebookConnect = {},
        onShowDisconnectDialog = {},
        onDismissDisconnectDialog = {},
        onConfirmDisconnect = {},
        onNavigateBack = {},
    )
}
