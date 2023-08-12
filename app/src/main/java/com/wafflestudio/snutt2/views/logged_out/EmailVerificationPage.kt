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
import com.wafflestudio.snutt2.views.*
import com.wafflestudio.snutt2.views.logged_in.home.settings.UserViewModel
import kotlinx.coroutines.launch

private enum class VerifyEmailState {
    AskContinue, SendCode,
}

@OptIn(ExperimentalAnimationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun EmailVerificationPage() {
    val navController = LocalNavController.current
    val focusManager = LocalFocusManager.current
    val apiOnProgress = LocalApiOnProgress.current
    val apiOnError = LocalApiOnError.current
    val context = LocalContext.current
    val keyboardManager = LocalSoftwareKeyboardController.current
    val onBackPressedDispatcherOwner = LocalOnBackPressedDispatcherOwner.current
    val coroutineScope = rememberCoroutineScope()
    val userViewModel = hiltViewModel<UserViewModel>()

    val userEmail = userViewModel.userInfo.collectAsState().value?.email ?: ""

    var flowState by remember { mutableStateOf(VerifyEmailState.AskContinue) }
    var codeField by remember { mutableStateOf("") }
    val buttonEnabled by remember { derivedStateOf { codeField.isNotEmpty() } }
    val timerState = rememberTimerState(
        initialValue = TimerValue.Initial,
        durationInSecond = 180,
    )

    val handleEnterCode = {
        coroutineScope.launch {
            if (codeField.isEmpty()) {
                context.toast(context.getString(R.string.find_password_enter_verification_code_empty_alert))
            } else if (timerState.isEnded) {
                context.toast(context.getString(R.string.find_password_enter_verification_code_expire_message))
            } else {
                launchSuspendApi(apiOnProgress, apiOnError) {
                    userViewModel.verifyEmailCode(codeField)
                    keyboardManager?.hide()
                    context.toast(context.getString(R.string.find_password_enter_verification_code_success_alert))
                    timerState.pause()
                    navController.navigateAsOrigin(NavigationDestination.Home)
                }
            }
        }
    }

    val onBackPressedCallback = remember {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                when (flowState) {
                    VerifyEmailState.AskContinue -> navController.navigateAsOrigin(NavigationDestination.Home)
                    VerifyEmailState.SendCode -> {
                        flowState = VerifyEmailState.AskContinue
                        codeField = ""
                        timerState.reset()
                    }
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
            .background(SNUTTColors.White900),
    ) {
        SimpleTopBar(
            title = stringResource(R.string.verify_email_app_bar_title),
            onClickNavigateBack = { onBackPressedCallback.handleOnBackPressed() },
        )
        AnimatedContent(targetState = flowState) { targetState ->
            Column(modifier = Modifier.padding(horizontal = 25.dp)) {
                when (targetState) {
                    VerifyEmailState.AskContinue -> {
                        Text(
                            text = stringResource(R.string.verify_email_question_text, userEmail),
                            style = SNUTTTypography.h3,
                            modifier = Modifier.padding(vertical = 25.dp),
                        )
                        Text(
                            text = stringResource(R.string.verify_email_detail_text),
                            style = SNUTTTypography.subtitle2.copy(color = SNUTTColors.Black900),
                        )
                        Spacer(
                            modifier = Modifier
                                .size(100.dp),
                        )
                        WebViewStyleButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                coroutineScope.launch {
                                    launchSuspendApi(apiOnProgress, apiOnError) {
                                        userViewModel.sendCodeToEmail(userEmail)
                                        flowState = VerifyEmailState.SendCode
                                        timerState.start()
                                    }
                                }
                            },
                        ) {
                            Text(
                                text = stringResource(R.string.verify_email_ok_button),
                                style = SNUTTTypography.button.copy(color = SNUTTColors.AllWhite),
                            )
                        }
                        Spacer(
                            modifier = Modifier
                                .size(20.dp),
                        )
                        WebViewStyleButton(
                            modifier = Modifier.fillMaxWidth(),
                            enabledColor = SNUTTColors.Gray200,
                            onClick = { navController.navigateAsOrigin(NavigationDestination.Home) },
                        ) {
                            Text(
                                text = stringResource(R.string.verify_email_later_button),
                                style = SNUTTTypography.button.copy(color = SNUTTColors.AllWhite),
                            )
                        }
                    }

                    VerifyEmailState.SendCode -> {
                        Text(
                            text = stringResource(R.string.find_password_verification_code_content).format(
                                userEmail,
                            ),
                            style = SNUTTTypography.h3,
                            modifier = Modifier.padding(vertical = 25.dp),
                        )
                        Text(
                            text = stringResource(R.string.find_password_send_code_label),
                            style = SNUTTTypography.h4,
                        )
                        EditText(
                            value = codeField,
                            onValueChange = { codeField = it },
                            hint = stringResource(R.string.find_password_send_code_hint),
                            keyboardActions = KeyboardActions(onNext = {
                                focusManager.moveFocus(
                                    FocusDirection.Down,
                                )
                            },),
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Done, keyboardType = KeyboardType.Number,
                            ),
                            singleLine = true,
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
                                                color = if (timerState.isRunning) {
                                                    SNUTTColors.Red
                                                } else {
                                                    SNUTTColors.SNUTTTheme
                                                },
                                            ),
                                            modifier = Modifier.clicks {
                                                if (timerState.isEnded) {
                                                    coroutineScope.launch {
                                                        launchSuspendApi(apiOnProgress, apiOnError) {
                                                            userViewModel.sendCodeToEmail(userEmail)
                                                            timerState.reset()
                                                            timerState.start()
                                                        }
                                                    }
                                                }
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
                            onClick = {
                                handleEnterCode()
                            },
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
