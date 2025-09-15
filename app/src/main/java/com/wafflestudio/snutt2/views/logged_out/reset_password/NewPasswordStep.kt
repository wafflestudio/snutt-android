package com.wafflestudio.snutt2.views.logged_out.reset_password

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
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.CustomDialog
import com.wafflestudio.snutt2.components.compose.EditText
import com.wafflestudio.snutt2.components.compose.Timer
import com.wafflestudio.snutt2.components.compose.TimerValue
import com.wafflestudio.snutt2.components.compose.WebViewStyleButton
import com.wafflestudio.snutt2.components.compose.rememberTimerState
import com.wafflestudio.snutt2.lib.data.SNUTTStringUtils.isPasswordInvalid
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography

@Composable
fun NewPasswordStep(
    onSubmit: (String) -> Unit,
    showCompleteDialog: State<Boolean>,
    onComplete: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val context = LocalContext.current

    var newPasswordField by remember { mutableStateOf("") }
    var newPasswordConfirmField by remember { mutableStateOf("") }

    var showErrorDialog by remember { mutableStateOf(false) }
    var errorDialogTitle by remember { mutableStateOf("") }

    val timerState = rememberTimerState(
        initialValue = TimerValue.Initial,
        durationInSecond = 180,
    )
    val buttonEnabled by remember {
        derivedStateOf {
            timerState.isRunning && newPasswordField.isNotBlank() && newPasswordConfirmField.isNotBlank()
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        timerState.start()
    }
    LaunchedEffect(timerState.currentValue) {
        if (timerState.isEnded) {
            errorDialogTitle = context.getString(R.string.find_password_enter_password_confirm_expired_alert)
            showErrorDialog = true
        }
    }
    LaunchedEffect(showCompleteDialog.value) {
        if (showCompleteDialog.value) {
            timerState.pause()
        }
    }

    val validateNewPasswordAndSubmit = {
        if (timerState.isRunning) {
            if (newPasswordField != newPasswordConfirmField) {
                errorDialogTitle = context.getString(R.string.find_password_enter_password_confirm_fail_alert)
                showErrorDialog = true
            } else if (newPasswordField.isPasswordInvalid()) {
                errorDialogTitle = context.getString(R.string.error_invalid_password)
                showErrorDialog = true
            } else {
                onSubmit(newPasswordField)
            }
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
            onValueChange = { newPasswordField = it },
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
            onValueChange = { newPasswordConfirmField = it },
            hint = stringResource(R.string.find_password_enter_password_confirm_hint),
            visualTransformation = PasswordVisualTransformation(),
            keyboardActions = KeyboardActions(
                onDone = {
                    validateNewPasswordAndSubmit()
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
                validateNewPasswordAndSubmit()
            },
        ) {
            Text(
                text = stringResource(R.string.common_ok),
                style = SNUTTTypography.h3.copy(color = if (buttonEnabled) SNUTTColors.AllWhite else SNUTTColors.VacancyGray),
            )
        }
    }

    if (showErrorDialog) {
        CustomDialog(
            title = errorDialogTitle,
            onConfirm = {
                showErrorDialog = false
                focusRequester.requestFocus()
            },
            onDismiss = {},
            positiveButtonText = stringResource(R.string.common_ok),
            negativeButtonText = null,
        ) {
        }
    }

    if (showCompleteDialog.value) {
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
