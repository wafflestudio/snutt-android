package com.wafflestudio.snutt2.feature.login

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.ui.components.compose.EditText
import com.wafflestudio.snutt2.ui.components.compose.SimpleTopBar
import com.wafflestudio.snutt2.ui.components.compose.Timer
import com.wafflestudio.snutt2.ui.components.compose.TimerState
import com.wafflestudio.snutt2.ui.components.compose.TimerValue
import com.wafflestudio.snutt2.ui.components.compose.WebViewStyleButton
import com.wafflestudio.snutt2.ui.components.compose.clicks
import com.wafflestudio.snutt2.ui.components.compose.rememberTimerState
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewSurface
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography
import com.wafflestudio.snutt2.ui.util.toast

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EmailVerificationPage(
    viewModel: EmailVerificationViewModel = hiltViewModel<EmailVerificationViewModel>(),
    onNavigateHome: () -> Unit,
) {
    val context = LocalContext.current
    val keyboardManager = LocalSoftwareKeyboardController.current
    val verificationSuccessMessage = stringResource(R.string.find_password_enter_verification_code_success_alert)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // TODO: TimerState 손보기
    val timerState = rememberTimerState(initialValue = TimerValue.Initial, durationInSecond = 180)

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is EmailVerificationUiEvent.ShowToast -> context.toast(event.message)
                is EmailVerificationUiEvent.RestartTimer -> {
                    timerState.reset()
                    timerState.start()
                }

                is EmailVerificationUiEvent.VerificationSuccess -> {
                    keyboardManager?.hide()
                    context.toast(verificationSuccessMessage)
                    timerState.pause()
                    onNavigateHome()
                }
            }
        }
    }

    EmailVerificationScreen(
        userEmail = viewModel.userEmail,
        flowState = uiState.flowState,
        codeField = uiState.codeField,
        timerState = timerState,
        onCodeFieldChange = viewModel::onCodeFieldChange,
        onSendCode = viewModel::sendCodeToEmail,
        onVerifyCode = viewModel::verifyEmailCode,
        onNavigateHome = onNavigateHome,
        onBackToAskContinue = {
            viewModel.backToAskContinue()
            timerState.reset()
        },
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun EmailVerificationScreen(
    userEmail: String,
    flowState: EmailVerificationUiState.FlowState,
    codeField: String,
    timerState: TimerState,
    onCodeFieldChange: (String) -> Unit,
    onSendCode: () -> Unit,
    onVerifyCode: () -> Unit,
    onNavigateHome: () -> Unit,
    onBackToAskContinue: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val emptyAlert = stringResource(R.string.find_password_enter_verification_code_empty_alert)
    val expireMessage = stringResource(R.string.find_password_enter_verification_code_expire_message)

    val buttonEnabled = codeField.isNotEmpty()

    val handleEnterCode = {
        if (codeField.isEmpty()) {
            context.toast(emptyAlert)
        } else if (timerState.isEnded) {
            context.toast(expireMessage)
        } else {
            onVerifyCode()
        }
    }

    val onBackPressed: () -> Unit = {
        when (flowState) {
            EmailVerificationUiState.FlowState.AskContinue -> onNavigateHome()
            EmailVerificationUiState.FlowState.SendCode -> onBackToAskContinue()
        }
    }

    BackHandler { onBackPressed() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SNUTTColors.White900),
    ) {
        SimpleTopBar(
            title = stringResource(R.string.verify_email_app_bar_title),
            onClickNavigateBack = { onBackPressed() },
        )
        AnimatedContent(targetState = flowState) { targetState ->
            Column(modifier = Modifier.padding(horizontal = 25.dp)) {
                when (targetState) {
                    EmailVerificationUiState.FlowState.AskContinue -> {
                        Text(
                            text = stringResource(R.string.verify_email_question_text, userEmail),
                            style = SNUTTTypography.h3,
                            modifier = Modifier.padding(vertical = 25.dp),
                        )
                        Text(
                            text = stringResource(R.string.verify_email_detail_text),
                            style = SNUTTTypography.subtitle2.copy(color = SNUTTColors.Black900),
                        )
                        Spacer(modifier = Modifier.size(100.dp))
                        WebViewStyleButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { onSendCode() },
                        ) {
                            Text(
                                text = stringResource(R.string.verify_email_ok_button),
                                style = SNUTTTypography.button.copy(color = SNUTTColors.AllWhite),
                            )
                        }
                        Spacer(modifier = Modifier.size(20.dp))
                        WebViewStyleButton(
                            modifier = Modifier.fillMaxWidth(),
                            enabledColor = SNUTTColors.Gray200,
                            onClick = { onNavigateHome() },
                        ) {
                            Text(
                                text = stringResource(R.string.verify_email_later_button),
                                style = SNUTTTypography.button.copy(color = SNUTTColors.AllWhite),
                            )
                        }
                    }

                    EmailVerificationUiState.FlowState.SendCode -> {
                        Text(
                            text = stringResource(R.string.find_password_verification_code_content).format(userEmail),
                            style = SNUTTTypography.h3,
                            modifier = Modifier.padding(vertical = 25.dp),
                        )
                        Text(
                            text = stringResource(R.string.find_password_send_code_label),
                            style = SNUTTTypography.h4,
                        )
                        EditText(
                            value = codeField,
                            onValueChange = onCodeFieldChange,
                            hint = stringResource(R.string.find_password_send_code_hint),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Number),
                            singleLine = true,
                            trailingIcon = {
                                Row(modifier = Modifier.padding(start = 10.dp)) {
                                    Timer(
                                        state = timerState,
                                        endMessage = stringResource(R.string.find_password_send_code_resend),
                                    ) { timerText ->
                                        Text(
                                            text = timerText,
                                            style = SNUTTTypography.subtitle2.copy(
                                                color = if (timerState.isRunning) SNUTTColors.Red else SNUTTColors.SNUTTTheme,
                                            ),
                                            modifier = Modifier.clicks {
                                                if (timerState.isEnded) onSendCode()
                                            },
                                        )
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                        )
                        if (timerState.isEnded) {
                            Text(
                                text = stringResource(R.string.find_password_enter_verification_code_expire_message),
                                style = SNUTTTypography.body2.copy(color = SNUTTColors.Red),
                            )
                        }
                        Spacer(modifier = Modifier.height(30.dp))
                        WebViewStyleButton(
                            modifier = Modifier.fillMaxWidth(),
                            enabled = buttonEnabled,
                            onClick = { handleEnterCode() },
                        ) {
                            Text(
                                text = stringResource(R.string.common_ok),
                                style = SNUTTTypography.h3.copy(color = SNUTTColors.AllWhite),
                            )
                        }
                    }
                }
            }
        }
    }
}

@SnuttPreview
@Composable
private fun EmailVerificationScreen_AskContinue() {
    SnuttPreviewSurface {
        EmailVerificationScreen(
            userEmail = "user@snu.ac.kr",
            flowState = EmailVerificationUiState.FlowState.AskContinue,
            codeField = "",
            timerState = rememberTimerState(initialValue = TimerValue.Initial, durationInSecond = 180),
            onCodeFieldChange = {},
            onSendCode = {},
            onVerifyCode = {},
            onNavigateHome = {},
            onBackToAskContinue = {},
        )
    }
}

@SnuttPreview
@Composable
private fun EmailVerificationScreen_SendCode() {
    SnuttPreviewSurface {
        EmailVerificationScreen(
            userEmail = "user@snu.ac.kr",
            flowState = EmailVerificationUiState.FlowState.SendCode,
            codeField = "",
            timerState = rememberTimerState(initialValue = TimerValue.Initial, durationInSecond = 180),
            onCodeFieldChange = {},
            onSendCode = {},
            onVerifyCode = {},
            onNavigateHome = {},
            onBackToAskContinue = {},
        )
    }
}
