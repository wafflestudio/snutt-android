package com.wafflestudio.snutt2.feature.login.resetpassword

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.feature.login.resetpassword.FindPasswordViewModel.UIState.CheckId
import com.wafflestudio.snutt2.feature.login.resetpassword.FindPasswordViewModel.UIState.EnterFullEmail
import com.wafflestudio.snutt2.feature.login.resetpassword.FindPasswordViewModel.UIState.EnterNewPassword
import com.wafflestudio.snutt2.feature.login.resetpassword.FindPasswordViewModel.UIState.VerifyCode
import com.wafflestudio.snutt2.ui.components.compose.IOSStyleTopBar
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewSurface
import com.wafflestudio.snutt2.ui.util.toast

@Composable
fun ResetPasswordPage(
    viewModel: FindPasswordViewModel = hiltViewModel<FindPasswordViewModel>(),
    onNavigateBack: () -> Unit,
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is FindPasswordUiEvent.ShowToast -> context.toast(event.message)
                is FindPasswordUiEvent.NavigateBack -> onNavigateBack()
            }
        }
    }

    val onBack = {
        if (uiState is CheckId) onNavigateBack() else viewModel.goToPreviousStep()
    }

    BackHandler { onBack() }

    ResetPasswordScreen(
        uiState = uiState,
        onIdFieldChange = viewModel::onIdFieldChange,
        onEmailFieldChange = viewModel::onEmailFieldChange,
        onCodeFieldChange = viewModel::onCodeFieldChange,
        onNewPasswordFieldChange = viewModel::onNewPasswordFieldChange,
        onNewPasswordConfirmFieldChange = viewModel::onNewPasswordConfirmFieldChange,
        onCheckEmailById = viewModel::checkEmailById,
        onSendFullEmail = viewModel::sendFullEmailAndRequestCode,
        onResendVerifyCode = viewModel::resendVerifyCode,
        onVerifyCode = viewModel::verifyCode,
        onShowWhyNotCodeComingDialog = viewModel::showWhyNotCodeComingDialog,
        onDismissVerifyCodeDialog = viewModel::dismissVerifyCodeDialog,
        onValidateAndResetPassword = viewModel::validateAndResetPassword,
        onTimerExpired = viewModel::onTimerExpired,
        onDismissNewPasswordDialog = viewModel::dismissNewPasswordDialog,
        onCompleteDialogConfirm = viewModel::onCompleteDialogConfirm,
        onBack = onBack,
    )
}

@Composable
private fun ResetPasswordScreen(
    uiState: FindPasswordViewModel.UIState,
    onIdFieldChange: (String) -> Unit,
    onEmailFieldChange: (String) -> Unit,
    onCodeFieldChange: (String) -> Unit,
    onNewPasswordFieldChange: (String) -> Unit,
    onNewPasswordConfirmFieldChange: (String) -> Unit,
    onCheckEmailById: () -> Unit,
    onSendFullEmail: () -> Unit,
    onResendVerifyCode: () -> Unit,
    onVerifyCode: () -> Unit,
    onShowWhyNotCodeComingDialog: () -> Unit,
    onDismissVerifyCodeDialog: () -> Unit,
    onValidateAndResetPassword: (timerRunning: Boolean) -> Unit,
    onTimerExpired: () -> Unit,
    onDismissNewPasswordDialog: () -> Unit,
    onCompleteDialogConfirm: () -> Unit,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding(),
    ) {
        IOSStyleTopBar(
            title = stringResource(R.string.find_password_title),
            backButtonText = when (uiState) {
                is CheckId -> stringResource(R.string.find_password_back_login)
                is EnterFullEmail -> stringResource(R.string.find_password_back_check_id)
                is VerifyCode -> stringResource(R.string.find_password_back_check_email)
                is EnterNewPassword -> stringResource(R.string.find_password_back_initial)
            },
        ) {
            onBack()
        }

        AnimatedContent(
            targetState = uiState,
            contentKey = { it::class },
            label = "",
        ) { state ->
            when (state) {
                is CheckId -> CheckIdStep(
                    uiState = state,
                    onIdFieldChange = onIdFieldChange,
                    onSubmit = onCheckEmailById,
                )

                is EnterFullEmail -> EnterFullEmailStep(
                    uiState = state,
                    onEmailFieldChange = onEmailFieldChange,
                    notMyEmail = onBack,
                    onSubmitFullEmail = onSendFullEmail,
                )

                is VerifyCode -> VerifyCodeStep(
                    uiState = state,
                    onCodeFieldChange = onCodeFieldChange,
                    onRequestResend = onResendVerifyCode,
                    onSubmit = onVerifyCode,
                    onShowWhyNotCodeComingDialog = onShowWhyNotCodeComingDialog,
                    onDismissDialog = onDismissVerifyCodeDialog,
                )

                is EnterNewPassword -> NewPasswordStep(
                    uiState = state,
                    onNewPasswordFieldChange = onNewPasswordFieldChange,
                    onNewPasswordConfirmFieldChange = onNewPasswordConfirmFieldChange,
                    onSubmit = onValidateAndResetPassword,
                    onTimerExpired = onTimerExpired,
                    onDismissDialog = onDismissNewPasswordDialog,
                    onComplete = onCompleteDialogConfirm,
                )
            }
        }
    }
}

@SnuttPreview
@Composable
private fun ResetPasswordScreen_CheckId() {
    SnuttPreviewSurface {
        ResetPasswordScreen(
            uiState = CheckId(userId = ""),
            onIdFieldChange = {},
            onEmailFieldChange = {},
            onCodeFieldChange = {},
            onNewPasswordFieldChange = {},
            onNewPasswordConfirmFieldChange = {},
            onCheckEmailById = {},
            onSendFullEmail = {},
            onResendVerifyCode = {},
            onVerifyCode = {},
            onShowWhyNotCodeComingDialog = {},
            onDismissVerifyCodeDialog = {},
            onValidateAndResetPassword = {},
            onTimerExpired = {},
            onDismissNewPasswordDialog = {},
            onCompleteDialogConfirm = {},
            onBack = {},
        )
    }
}

@SnuttPreview
@Composable
private fun ResetPasswordScreen_EnterFullEmail() {
    SnuttPreviewSurface {
        ResetPasswordScreen(
            uiState = EnterFullEmail(userId = "testuser", maskedEmail = "te****@snu.ac.kr", fullEmail = ""),
            onIdFieldChange = {},
            onEmailFieldChange = {},
            onCodeFieldChange = {},
            onNewPasswordFieldChange = {},
            onNewPasswordConfirmFieldChange = {},
            onCheckEmailById = {},
            onSendFullEmail = {},
            onResendVerifyCode = {},
            onVerifyCode = {},
            onShowWhyNotCodeComingDialog = {},
            onDismissVerifyCodeDialog = {},
            onValidateAndResetPassword = {},
            onTimerExpired = {},
            onDismissNewPasswordDialog = {},
            onCompleteDialogConfirm = {},
            onBack = {},
        )
    }
}

@SnuttPreview
@Composable
private fun ResetPasswordScreen_VerifyCode() {
    SnuttPreviewSurface {
        ResetPasswordScreen(
            uiState = VerifyCode(fullEmail = "testuser@snu.ac.kr"),
            onIdFieldChange = {},
            onEmailFieldChange = {},
            onCodeFieldChange = {},
            onNewPasswordFieldChange = {},
            onNewPasswordConfirmFieldChange = {},
            onCheckEmailById = {},
            onSendFullEmail = {},
            onResendVerifyCode = {},
            onVerifyCode = {},
            onShowWhyNotCodeComingDialog = {},
            onDismissVerifyCodeDialog = {},
            onValidateAndResetPassword = {},
            onTimerExpired = {},
            onDismissNewPasswordDialog = {},
            onCompleteDialogConfirm = {},
            onBack = {},
        )
    }
}

@SnuttPreview
@Composable
private fun ResetPasswordScreen_EnterNewPassword() {
    SnuttPreviewSurface {
        ResetPasswordScreen(
            uiState = EnterNewPassword(),
            onIdFieldChange = {},
            onEmailFieldChange = {},
            onCodeFieldChange = {},
            onNewPasswordFieldChange = {},
            onNewPasswordConfirmFieldChange = {},
            onCheckEmailById = {},
            onSendFullEmail = {},
            onResendVerifyCode = {},
            onVerifyCode = {},
            onShowWhyNotCodeComingDialog = {},
            onDismissVerifyCodeDialog = {},
            onValidateAndResetPassword = {},
            onTimerExpired = {},
            onDismissNewPasswordDialog = {},
            onCompleteDialogConfirm = {},
            onBack = {},
        )
    }
}
