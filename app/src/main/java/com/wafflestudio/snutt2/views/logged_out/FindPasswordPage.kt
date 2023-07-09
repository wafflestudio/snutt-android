package com.wafflestudio.snutt2.views.logged_out

import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.*
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.*
import com.wafflestudio.snutt2.lib.android.toast
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.views.LocalApiOnError
import com.wafflestudio.snutt2.views.LocalApiOnProgress
import com.wafflestudio.snutt2.views.LocalNavController
import com.wafflestudio.snutt2.views.launchSuspendApi
import com.wafflestudio.snutt2.views.logged_in.home.settings.UserViewModel
import kotlinx.coroutines.launch

private enum class FlowState {
    CheckEmail, SendCode, ResetPassword,
}

@OptIn(ExperimentalAnimationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun FindPasswordPage() {
    val navController = LocalNavController.current
    val apiOnError = LocalApiOnError.current
    val apiOnProgress = LocalApiOnProgress.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val keyboardManager = LocalSoftwareKeyboardController.current
    val onBackPressedDispatcherOwner = LocalOnBackPressedDispatcherOwner.current

    val userViewModel = hiltViewModel<UserViewModel>()

    var flowState by remember { mutableStateOf(FlowState.CheckEmail) }

    var idField by remember { mutableStateOf("") }
    var codeField by remember { mutableStateOf("") }
    var passwordField by remember { mutableStateOf("") }
    var passwordConfirmField by remember { mutableStateOf("") }
    var checkEmailDialogState by remember { mutableStateOf(false) }

    var emailResponse by remember { mutableStateOf("") }

    val timerState = rememberTimerState(
        initialValue = TimerValue.Initial,
        endTime = 180,
    )
    val handleCheckEmailById = {
        coroutineScope.launch {
            if (idField.isEmpty()) {
                context.toast(context.getString(R.string.find_password_enter_id_hint))
            } else {
                launchSuspendApi(apiOnProgress, apiOnError) {
                    emailResponse = userViewModel.checkEmailById(idField)
                    keyboardManager?.hide()
                    checkEmailDialogState = true
                }
            }
        }
    }

    val handleEnterCode = {
        coroutineScope.launch {
            if (codeField.isEmpty()) {
                context.toast(context.getString(R.string.find_password_enter_verification_code_empty_alert))
            } else if (timerState.isEnded) {
                context.toast(context.getString(R.string.find_password_enter_verification_code_expire_message))
            } else {
                launchSuspendApi(apiOnProgress, apiOnError) {
                    userViewModel.verifyPwResetCode(idField, codeField)
                    keyboardManager?.hide()
                    context.toast(context.getString(R.string.find_password_enter_verification_code_success_alert))
                    timerState.pause()
                    flowState = FlowState.ResetPassword
                }
            }
        }
    }

    val handleResetPassword = {
        coroutineScope.launch {
            if (passwordField.isEmpty() || passwordConfirmField.isEmpty()) {
                context.toast(context.getString(R.string.find_password_enter_password_empty_alert))
            } else if (passwordConfirmField != passwordField) {
                context.toast(context.getString(R.string.find_password_enter_password_confirm_fail_alert))
            } else {
                launchSuspendApi(apiOnProgress, apiOnError) {
                    userViewModel.resetPassword(idField, passwordField)
                    keyboardManager?.hide()
                    context.toast(context.getString(R.string.find_password_enter_password_success_alert))
                    navController.popBackStack()
                }
            }
        }
    }

    val onBackPressedCallback = remember {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                when (flowState) {
                    FlowState.CheckEmail -> navController.popBackStack()
                    FlowState.SendCode -> {
                        flowState = FlowState.CheckEmail
                        codeField = ""
                        timerState.reset()
                    }
                    FlowState.ResetPassword -> flowState = FlowState.SendCode
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onBackPressedDispatcherOwner?.onBackPressedDispatcher?.addCallback(onBackPressedCallback)
        onDispose { onBackPressedCallback.remove() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SNUTTColors.White900)
            .clicks { focusManager.clearFocus() }
    ) {
        SimpleTopBar(title = stringResource(R.string.find_password_title), onClickNavigateBack = {
            onBackPressedCallback.handleOnBackPressed()
        })
        AnimatedContent(targetState = flowState) { targetState ->
            Column(modifier = Modifier.padding(horizontal = 25.dp)) {
                when (targetState) {
                    FlowState.CheckEmail -> {
                        Text(
                            text = stringResource(R.string.find_password_check_email_content),
                            style = SNUTTTypography.h3,
                            modifier = Modifier.padding(vertical = 25.dp),
                        )
                        Text(
                            text = stringResource(R.string.sign_in_id_hint),
                            style = SNUTTTypography.h4
                        )
                        EditText(
                            value = idField,
                            onValueChange = { idField = it },
                            hint = stringResource(R.string.find_password_enter_id_hint),
                            keyboardActions = KeyboardActions(onNext = {
                                focusManager.moveFocus(
                                    FocusDirection.Down
                                )
                            }),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                        )
                    }
                    FlowState.SendCode -> {
                        Text(
                            text = stringResource(R.string.find_password_verification_code_content).format(
                                emailResponse
                            ),
                            style = SNUTTTypography.h3,
                            modifier = Modifier.padding(vertical = 25.dp),
                        )
                        Text(
                            text = stringResource(R.string.find_password_send_code_label),
                            style = SNUTTTypography.h4
                        )
                        EditText(
                            value = codeField,
                            onValueChange = { codeField = it },
                            hint = stringResource(R.string.find_password_send_code_hint),
                            keyboardActions = KeyboardActions(onNext = {
                                focusManager.moveFocus(
                                    FocusDirection.Down
                                )
                            }),
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Done, keyboardType = KeyboardType.Ascii
                            ),
                            singleLine = true,
                            trailingIcon = {
                                Row(
                                    modifier = Modifier.padding(start = 10.dp)
                                ) {
                                    Timer(
                                        state = timerState,
                                        endMessage = stringResource(R.string.find_password_send_code_resend),
                                    ) { timerText ->
                                        Text(
                                            text = timerText,
                                            style = SNUTTTypography.subtitle2.copy(
                                                color = if (timerState.isRunning) SNUTTColors.Red
                                                else SNUTTColors.SNUTTTheme
                                            ),
                                            modifier = Modifier.clicks {
                                                if (timerState.isEnded) {
                                                    coroutineScope.launch {
                                                        launchSuspendApi(apiOnProgress, apiOnError) {
                                                            userViewModel.sendPwResetCodeToEmail(emailResponse)
                                                            timerState.reset()
                                                            timerState.start()
                                                        }
                                                    }
                                                }
                                            }
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
                                style = SNUTTTypography.body2.copy(color = SNUTTColors.Red)
                            )
                        }
                    }
                    FlowState.ResetPassword -> {
                        Spacer(modifier = Modifier.height(25.dp))
                        Text(
                            text = stringResource(R.string.find_password_enter_password_label),
                            style = SNUTTTypography.h4
                        )
                        EditText(
                            value = passwordField,
                            onValueChange = { passwordField = it },
                            hint = stringResource(R.string.sign_up_password_hint),
                            keyboardActions = KeyboardActions(onNext = {
                                focusManager.moveFocus(
                                    FocusDirection.Down
                                )
                            }),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = stringResource(R.string.find_password_enter_password_confirm_label),
                            style = SNUTTTypography.h4
                        )
                        EditText(
                            value = passwordConfirmField,
                            onValueChange = { passwordConfirmField = it },
                            hint = stringResource(R.string.sign_up_password_confirm_hint),
                            keyboardActions = KeyboardActions(onNext = {
                                focusManager.moveFocus(
                                    FocusDirection.Down
                                )
                            }),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                        )
                    }
                }
                Spacer(modifier = Modifier.height(30.dp))
                WebViewStyleButton(
                    modifier = Modifier.fillMaxWidth(),
                    color = when (flowState) {
                        FlowState.CheckEmail -> if (idField.isEmpty()) SNUTTColors.Gray400 else SNUTTColors.SNUTTTheme
                        FlowState.SendCode -> if (codeField.isEmpty()) SNUTTColors.Gray400 else SNUTTColors.SNUTTTheme
                        FlowState.ResetPassword -> if (passwordField.isEmpty() || passwordConfirmField.isEmpty()) SNUTTColors.Gray400 else SNUTTColors.SNUTTTheme
                    },
                    onClick = {
                        when (flowState) {
                            FlowState.CheckEmail -> handleCheckEmailById()
                            FlowState.SendCode -> handleEnterCode()
                            FlowState.ResetPassword -> handleResetPassword()
                        }
                    }
                ) {
                    Text(
                        text = stringResource(R.string.common_ok),
                        style = SNUTTTypography.h3.copy(color = SNUTTColors.AllWhite)
                    )
                }
            }
        }
    }

    if (checkEmailDialogState) {
        CustomDialog(
            title = stringResource(R.string.find_password_check_email_dialog_title),
            onDismiss = { checkEmailDialogState = false },
            onConfirm = {
                checkEmailDialogState = false
                coroutineScope.launch {
                    launchSuspendApi(apiOnProgress, apiOnError) {
                        userViewModel.sendPwResetCodeToEmail(emailResponse)
                        flowState = FlowState.SendCode
                        timerState.start()
                    }
                }
            },
            positiveButtonText = stringResource(R.string.common_ok),
            negativeButtonText = stringResource(R.string.find_password_check_email_dialog_negative)
        ) {
            Text(text = stringResource(R.string.find_password_check_email_dialog_content).format(emailResponse), style = SNUTTTypography.body1)
        }
    }
}
