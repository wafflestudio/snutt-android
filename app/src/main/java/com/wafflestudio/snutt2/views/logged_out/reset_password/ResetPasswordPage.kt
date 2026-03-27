package com.wafflestudio.snutt2.views.logged_out.reset_password

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.IOSStyleTopBar
import com.wafflestudio.snutt2.views.LocalApiOnError
import com.wafflestudio.snutt2.views.LocalApiOnProgress
import com.wafflestudio.snutt2.views.launchSuspendApi
import com.wafflestudio.snutt2.views.logged_out.reset_password.FindPasswordViewModel.UIState.CheckId
import com.wafflestudio.snutt2.views.logged_out.reset_password.FindPasswordViewModel.UIState.EnterFullEmail
import com.wafflestudio.snutt2.views.logged_out.reset_password.FindPasswordViewModel.UIState.EnterNewPassword
import com.wafflestudio.snutt2.views.logged_out.reset_password.FindPasswordViewModel.UIState.VerifyCode
import kotlinx.coroutines.launch

@Composable
fun ResetPasswordPage(
    onNavigateBack: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val apiOnProgress = LocalApiOnProgress.current
    val apiOnError = LocalApiOnError.current
    val viewModel = hiltViewModel<FindPasswordViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BackHandler {
        if (uiState is CheckId) {
            onNavigateBack()
        } else {
            viewModel.goToPreviousStep()
        }
    }

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
            if (uiState is CheckId) {
                onNavigateBack()
            } else {
                viewModel.goToPreviousStep()
            }
        }

        AnimatedContent(targetState = uiState, label = "") { state ->
            when (state) {
                is CheckId -> CheckIdStep(
                    uiState = state,
                    onSubmit = { userId ->
                        scope.launch {
                            launchSuspendApi(apiOnProgress, apiOnError) {
                                viewModel.checkEmailById(userId)
                            }
                        }
                    },
                )

                is EnterFullEmail -> EnterFullEmailStep(
                    uiState = state,
                    notMyEmail = viewModel::goToPreviousStep,
                    onSubmitFullEmail = { fullEmail ->
                        scope.launch {
                            launchSuspendApi(apiOnProgress, apiOnError) {
                                viewModel.sendFullEmailAndRequestCode(fullEmail)
                            }
                        }
                    },
                )

                is VerifyCode -> VerifyCodeStep(
                    uiState = state,
                    onRequestResend = {
                        scope.launch {
                            launchSuspendApi(apiOnProgress, apiOnError) {
                                viewModel.sendFullEmailAndRequestCode(state.fullEmail)
                            }
                        }
                    },
                    onSubmit = { code ->
                        scope.launch {
                            launchSuspendApi(apiOnProgress, apiOnError) {
                                viewModel.verifyCode(code)
                            }
                        }
                    },
                )

                is EnterNewPassword -> {
                    val showCompleteDialog = remember { mutableStateOf(false) }
                    NewPasswordStep(
                        onSubmit = { newPassword ->
                            scope.launch {
                                launchSuspendApi(apiOnProgress, apiOnError) {
                                    viewModel.resetPassword(newPassword)
                                    showCompleteDialog.value = true
                                }
                            }
                        },
                        showCompleteDialog = showCompleteDialog,
                        onComplete = {
                            showCompleteDialog.value = false
                            onNavigateBack()
                        },
                    )
                }
            }
        }
    }
}
