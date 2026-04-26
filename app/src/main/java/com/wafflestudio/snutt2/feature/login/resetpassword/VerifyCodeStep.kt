package com.wafflestudio.snutt2.feature.login.resetpassword

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.ui.components.compose.CustomDialog
import com.wafflestudio.snutt2.ui.components.compose.EditText
import com.wafflestudio.snutt2.ui.components.compose.Timer
import com.wafflestudio.snutt2.ui.components.compose.TimerValue
import com.wafflestudio.snutt2.ui.components.compose.WebViewStyleButton
import com.wafflestudio.snutt2.ui.components.compose.clicks
import com.wafflestudio.snutt2.ui.components.compose.rememberTimerState
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewSurface
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun VerifyCodeStep(
    uiState: FindPasswordViewModel.UIState.VerifyCode,
    onCodeFieldChange: (String) -> Unit,
    onRequestResend: () -> Unit,
    onSubmit: () -> Unit,
    onShowWhyNotCodeComingDialog: () -> Unit,
    onDismissDialog: () -> Unit,
) {
    val keyboardManager = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    val codeField = uiState.codeField
    val isWhyNotCodeComingDialogShown = uiState.dialogState is FindPasswordViewModel.UIState.VerifyCode.VerifyCodeDialogState.WhyNotCodeComing
    val timerState = rememberTimerState(
        initialValue = TimerValue.Initial,
        durationInSecond = 180,
    )
    val buttonEnabled = codeField.length == 8 && timerState.isRunning

    LaunchedEffect(Unit) {
        timerState.start()
    }
    LaunchedEffect(isWhyNotCodeComingDialogShown) {
        if (!isWhyNotCodeComingDialogShown) {
            focusRequester.requestFocus()
            keyboardManager?.show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 44.dp, horizontal = 20.dp),
    ) {
        Text(
            text = stringResource(R.string.find_password_verification_code_content, uiState.fullEmail),
            style = SNUTTTypography.h2.copy(fontSize = 17.sp),
        )

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = stringResource(R.string.find_password_send_code_label),
            style = SNUTTTypography.body1.copy(color = SNUTTColors.EditTextLabel),
        )

        Spacer(modifier = Modifier.height(12.dp))

        EditText(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            value = codeField,
            onValueChange = onCodeFieldChange,
            hint = stringResource(R.string.find_password_send_code_hint),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (buttonEnabled) {
                        onSubmit()
                    }
                },
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            singleLine = true,
            underlineColorFocused = if (codeField.isBlank()) SNUTTColors.EditTextUnderline else SNUTTColors.SNUTTTheme,
            trailingIcon = {
                Row(
                    modifier = Modifier.padding(start = 10.dp),
                ) {
                    Timer(
                        state = timerState,
                        endMessage = stringResource(R.string.find_password_send_code_resend),
                    ) { timerText ->
                        Text(
                            text = timerText,
                            style = SNUTTTypography.subtitle2.copy(
                                fontSize = 15.sp,
                                color = if (timerState.isRunning) {
                                    SNUTTColors.Red
                                } else {
                                    SNUTTColors.SNUTTTheme
                                },
                            ),
                            modifier = Modifier
                                .clicks {
                                    if (timerState.isEnded) {
                                        onRequestResend()
                                        timerState.reset()
                                        timerState.start()
                                    }
                                }
                                .padding(end = 10.dp),
                        )
                    }
                }
            },
        )

        if (timerState.isEnded) {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.find_password_enter_verification_code_expire_message),
                style = SNUTTTypography.body1.copy(fontSize = 13.sp, color = SNUTTColors.Red),
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        WebViewStyleButton(
            modifier = Modifier.fillMaxWidth(),
            enabled = buttonEnabled,
            onClick = {
                onSubmit()
            },
        ) {
            Text(
                text = stringResource(R.string.common_ok),
                style = SNUTTTypography.h3.copy(color = if (buttonEnabled) SNUTTColors.AllWhite else SNUTTColors.VacancyGray),
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text(
                text = stringResource(R.string.find_password_send_code_not_coming),
                style = SNUTTTypography.body1.copy(color = SNUTTColors.VacancyGray),
                modifier = Modifier.clicks {
                    onShowWhyNotCodeComingDialog()
                },
            )
        }
    }

    if (isWhyNotCodeComingDialogShown) {
        CustomDialog(
            title = stringResource(R.string.find_password_enter_verification_code_why_not_coming),
            onConfirm = onDismissDialog,
            onDismiss = {},
            positiveButtonText = stringResource(R.string.common_ok),
            negativeButtonText = null,
        ) {
        }
    }
}

@SnuttPreview
@Composable
private fun VerifyCodeStep_Default() {
    SnuttPreviewSurface {
        VerifyCodeStep(
            uiState = FindPasswordViewModel.UIState.VerifyCode(fullEmail = "snutt_user@snu.ac.kr"),
            onCodeFieldChange = {},
            onRequestResend = {},
            onSubmit = {},
            onShowWhyNotCodeComingDialog = {},
            onDismissDialog = {},
        )
    }
}
