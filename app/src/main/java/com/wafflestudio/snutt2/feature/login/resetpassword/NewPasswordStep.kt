package com.wafflestudio.snutt2.feature.login.resetpassword

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.ui.components.compose.CustomDialog
import com.wafflestudio.snutt2.ui.components.compose.EditText
import com.wafflestudio.snutt2.ui.components.compose.Timer
import com.wafflestudio.snutt2.ui.components.compose.TimerValue
import com.wafflestudio.snutt2.ui.components.compose.WebViewStyleButton
import com.wafflestudio.snutt2.ui.components.compose.rememberTimerState
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewSurface
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography

@Composable
fun NewPasswordStep(
    uiState: FindPasswordViewModel.UIState.EnterNewPassword,
    onNewPasswordFieldChange: (String) -> Unit,
    onNewPasswordConfirmFieldChange: (String) -> Unit,
    onSubmit: (timerRunning: Boolean) -> Unit,
    onTimerExpired: () -> Unit,
    onDismissDialog: () -> Unit,
    onComplete: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    val newPasswordField = uiState.newPasswordField
    val newPasswordConfirmField = uiState.newPasswordConfirmField
    val dialogState = uiState.dialogState
    val isCompleteDialogShown = dialogState is FindPasswordViewModel.UIState.EnterNewPassword.NewPasswordDialogState.Complete

    val timerState = rememberTimerState(
        initialValue = TimerValue.Initial,
        durationInSecond = 180,
    )
    val buttonEnabled = timerState.isRunning && newPasswordField.isNotBlank() && newPasswordConfirmField.isNotBlank()

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        timerState.start()
    }
    LaunchedEffect(timerState.currentValue) {
        if (timerState.isEnded) {
            onTimerExpired()
        }
    }
    LaunchedEffect(isCompleteDialogShown) {
        if (isCompleteDialogShown) {
            timerState.pause()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 44.dp, horizontal = 20.dp),
    ) {
        Text(
            text = stringResource(R.string.find_password_enter_password_body),
            style = SNUTTTypography.h2.copy(fontSize = 17.sp),
        )

        Spacer(modifier = Modifier.height(40.dp))

        EditText(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            value = newPasswordField,
            onValueChange = onNewPasswordFieldChange,
            hint = stringResource(R.string.find_password_enter_password_hint),
            visualTransformation = PasswordVisualTransformation(),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                },
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            singleLine = true,
            underlineColor = SNUTTColors.EditTextUnderline,
            underlineColorFocused = if (newPasswordField.isBlank()) SNUTTColors.EditTextUnderline else SNUTTColors.SNUTTTheme,
            trailingIcon = {
                Row(
                    modifier = Modifier.padding(start = 10.dp),
                ) {
                    Timer(
                        state = timerState,
                        endMessage = stringResource(R.string.find_password_enter_password_confirm_expired),
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
                                .padding(end = 10.dp),
                        )
                    }
                }
            },
        )

        Spacer(modifier = Modifier.height(24.dp))

        EditText(
            modifier = Modifier.fillMaxWidth(),
            value = newPasswordConfirmField,
            onValueChange = onNewPasswordConfirmFieldChange,
            hint = stringResource(R.string.find_password_enter_password_confirm_hint),
            visualTransformation = PasswordVisualTransformation(),
            keyboardActions = KeyboardActions(
                onDone = {
                    onSubmit(timerState.isRunning)
                },
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            singleLine = true,
            underlineColor = SNUTTColors.EditTextUnderline,
            underlineColorFocused = if (newPasswordConfirmField.isBlank()) SNUTTColors.EditTextUnderline else SNUTTColors.SNUTTTheme,
        )

        Spacer(modifier = Modifier.height(48.dp))

        WebViewStyleButton(
            modifier = Modifier.fillMaxWidth(),
            enabled = buttonEnabled,
            onClick = {
                onSubmit(timerState.isRunning)
            },
        ) {
            Text(
                text = stringResource(R.string.common_ok),
                style = SNUTTTypography.h3.copy(color = if (buttonEnabled) SNUTTColors.AllWhite else SNUTTColors.VacancyGray),
            )
        }
    }

    if (dialogState is FindPasswordViewModel.UIState.EnterNewPassword.NewPasswordDialogState.Error) {
        val errorTitle = when (dialogState.type) {
            FindPasswordViewModel.UIState.EnterNewPassword.ErrorType.Expired ->
                stringResource(R.string.find_password_enter_password_confirm_expired_alert)
            FindPasswordViewModel.UIState.EnterNewPassword.ErrorType.ConfirmFail ->
                stringResource(R.string.find_password_enter_password_confirm_fail_alert)
            FindPasswordViewModel.UIState.EnterNewPassword.ErrorType.InvalidPassword ->
                stringResource(R.string.error_invalid_password)
        }
        CustomDialog(
            title = errorTitle,
            onConfirm = {
                onDismissDialog()
                focusRequester.requestFocus()
            },
            onDismiss = {},
            positiveButtonText = stringResource(R.string.common_ok),
            negativeButtonText = null,
        ) {
        }
    }

    if (isCompleteDialogShown) {
        CustomDialog(
            title = stringResource(R.string.find_password_enter_password_success_alert),
            onConfirm = onComplete,
            onDismiss = {},
            positiveButtonText = stringResource(R.string.common_ok),
            negativeButtonText = null,
        ) {
        }
    }
}

@SnuttPreview
@Composable
private fun NewPasswordStep_Default() {
    SnuttPreviewSurface {
        NewPasswordStep(
            uiState = FindPasswordViewModel.UIState.EnterNewPassword(),
            onNewPasswordFieldChange = {},
            onNewPasswordConfirmFieldChange = {},
            onSubmit = {},
            onTimerExpired = {},
            onDismissDialog = {},
            onComplete = {},
        )
    }
}
