package com.wafflestudio.snutt2.views.logged_out.reset_password

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.SimpleTopBar
import com.wafflestudio.snutt2.views.LocalApiOnError
import com.wafflestudio.snutt2.views.LocalApiOnProgress
import com.wafflestudio.snutt2.views.LocalNavController
import com.wafflestudio.snutt2.views.launchSuspendApi
import com.wafflestudio.snutt2.views.logged_out.reset_password.FindPasswordViewModel.UIState.CheckId
import com.wafflestudio.snutt2.views.logged_out.reset_password.FindPasswordViewModel.UIState.EnterFullEmail
import com.wafflestudio.snutt2.views.logged_out.reset_password.FindPasswordViewModel.UIState.VerifyCode
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ResetPasswordPage() {
    val scope = rememberCoroutineScope()
    val apiOnProgress = LocalApiOnProgress.current
    val apiOnError = LocalApiOnError.current
    val keyboardManager = LocalSoftwareKeyboardController.current
    val navController = LocalNavController.current
    val viewModel = hiltViewModel<FindPasswordViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        SimpleTopBar(
            title = stringResource(R.string.find_password_title),
            onClickNavigateBack = {
                if (uiState is CheckId) {
                    navController.popBackStack()
                } else {
                    viewModel.goToPreviousStep()
                }
            },
        )

        AnimatedContent(targetState = uiState, label = "") { state ->
            when (state) {
                is CheckId -> CheckIdStep(
                    userId = state.userId,
                    onSubmit = { userId ->
                        scope.launch {
                            launchSuspendApi(apiOnProgress, apiOnError) {
                                viewModel.checkEmailById(userId)
                                keyboardManager?.hide()
                            }
                        }
                    },
                )

                is EnterFullEmail -> EnterFullEmailStep(
                    userId = state.userId,
                    maskedEmail = state.maskedEmail,
                    fullEmail = state.fullEmail,
                    notMyEmail = viewModel::goToPreviousStep,
                    onSubmitFullEmail = { fullEmail ->
                        scope.launch {
                            launchSuspendApi(apiOnProgress, apiOnError) {
                                viewModel.sendFullEmailAndRequestCode(fullEmail)
                                keyboardManager?.hide()
                            }
                        }
                    },
                )

                is VerifyCode -> VerifyCodeStep(
                    fullEmail = state.fullEmail,
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
            }
        }
    }
}
