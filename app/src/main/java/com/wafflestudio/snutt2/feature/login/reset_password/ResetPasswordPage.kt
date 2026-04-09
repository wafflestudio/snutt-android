package com.wafflestudio.snutt2.feature.login.reset_password

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.feature.login.reset_password.FindPasswordViewModel.UIState.CheckId
import com.wafflestudio.snutt2.feature.login.reset_password.FindPasswordViewModel.UIState.EnterFullEmail
import com.wafflestudio.snutt2.feature.login.reset_password.FindPasswordViewModel.UIState.EnterNewPassword
import com.wafflestudio.snutt2.feature.login.reset_password.FindPasswordViewModel.UIState.VerifyCode
import com.wafflestudio.snutt2.ui.components.compose.IOSStyleTopBar
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
        onCheckEmailById = { viewModel.checkEmailById(it) },
        onSendFullEmail = { viewModel.sendFullEmailAndRequestCode(it) },
        onVerifyCode = { viewModel.verifyCode(it) },
        onResetPassword = { viewModel.resetPassword(it) },
        onCompleteDialogConfirm = { viewModel.onCompleteDialogConfirm() },
        onBack = onBack,
    )
}

@Composable
private fun ResetPasswordScreen(
    uiState: FindPasswordViewModel.UIState,
    onCheckEmailById: (String) -> Unit,
    onSendFullEmail: (String) -> Unit,
    onVerifyCode: (String) -> Unit,
    onResetPassword: (String) -> Unit,
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

        AnimatedContent(targetState = uiState, label = "") { state ->
            when (state) {
                is CheckId -> CheckIdStep(
                    uiState = state,
                    onSubmit = onCheckEmailById,
                )

                is EnterFullEmail -> EnterFullEmailStep(
                    uiState = state,
                    notMyEmail = onBack,
                    onSubmitFullEmail = onSendFullEmail,
                )

                is VerifyCode -> VerifyCodeStep(
                    uiState = state,
                    onRequestResend = { onSendFullEmail(state.fullEmail) },
                    onSubmit = onVerifyCode,
                )

                is EnterNewPassword -> {
                    val showCompleteDialog = remember(state) {
                        derivedStateOf { state.showCompleteDialog }
                    }
                    NewPasswordStep(
                        onSubmit = onResetPassword,
                        showCompleteDialog = showCompleteDialog,
                        onComplete = onCompleteDialogConfirm,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "CheckId")
@Composable
private fun ResetPasswordScreenCheckIdPreview() {
    ResetPasswordScreen(
        uiState = CheckId(userId = ""),
        onCheckEmailById = {},
        onSendFullEmail = {},
        onVerifyCode = {},
        onResetPassword = {},
        onCompleteDialogConfirm = {},
        onBack = {},
    )
}

@Preview(showBackground = true, name = "EnterFullEmail")
@Composable
private fun ResetPasswordScreenEnterFullEmailPreview() {
    ResetPasswordScreen(
        uiState = EnterFullEmail(userId = "testuser", maskedEmail = "te****@snu.ac.kr", fullEmail = ""),
        onCheckEmailById = {},
        onSendFullEmail = {},
        onVerifyCode = {},
        onResetPassword = {},
        onCompleteDialogConfirm = {},
        onBack = {},
    )
}

@Preview(showBackground = true, name = "VerifyCode")
@Composable
private fun ResetPasswordScreenVerifyCodePreview() {
    ResetPasswordScreen(
        uiState = VerifyCode(fullEmail = "testuser@snu.ac.kr"),
        onCheckEmailById = {},
        onSendFullEmail = {},
        onVerifyCode = {},
        onResetPassword = {},
        onCompleteDialogConfirm = {},
        onBack = {},
    )
}

@Preview(showBackground = true, name = "EnterNewPassword")
@Composable
private fun ResetPasswordScreenEnterNewPasswordPreview() {
    ResetPasswordScreen(
        uiState = EnterNewPassword(),
        onCheckEmailById = {},
        onSendFullEmail = {},
        onVerifyCode = {},
        onResetPassword = {},
        onCompleteDialogConfirm = {},
        onBack = {},
    )
}
