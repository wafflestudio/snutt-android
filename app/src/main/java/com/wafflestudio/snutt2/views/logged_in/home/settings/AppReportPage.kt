package com.wafflestudio.snutt2.views.logged_in.home.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.ArrowBackIcon
import com.wafflestudio.snutt2.components.compose.EditText
import com.wafflestudio.snutt2.components.compose.SendIcon
import com.wafflestudio.snutt2.components.compose.TopBar
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.lib.android.toast
import com.wafflestudio.snutt2.lib.data.SNUTTStringUtils.isEmailInvalid
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.views.LocalApiOnError
import com.wafflestudio.snutt2.views.LocalApiOnProgress
import com.wafflestudio.snutt2.views.LocalNavController
import com.wafflestudio.snutt2.views.launchSuspendApi
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AppReportPage() {
    val context = LocalContext.current
    val navController = LocalNavController.current
    val apiOnError = LocalApiOnError.current
    val apiOnProgress = LocalApiOnProgress.current
    val keyboardManager = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val userViewModel = hiltViewModel<UserViewModel>()

    var email by remember { mutableStateOf(userViewModel.userInfo.value?.email ?: "") }
    var detail by remember { mutableStateOf("") }

    var sentEnabled by remember { mutableStateOf(true) }
    val sendFeedback = {
        if (detail.isEmpty()) {
            context.toast(context.getString(R.string.feedback_empty_detail_warning))
        } else if (email.isEmailInvalid()) {
            context.toast(context.getString(R.string.feedback_invalid_email_warning))
        } else {
            sentEnabled = false
            scope.launch {
                launchSuspendApi(
                    apiOnProgress,
                    apiOnError,
                    onError = {
                        sentEnabled = true
                        apiOnProgress.hideProgress()
                    },
                    context.getString(R.string.settings_app_report_loading_indicator_title),
                ) {
                    userViewModel.sendFeedback(email, detail)
                    keyboardManager?.hide()
                    context.toast(context.getString(R.string.feedback_send_success_message))
                    navController.popBackStack()
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SNUTTColors.White900),
    ) {
        TopBar(
            title = {
                Text(
                    text = stringResource(R.string.settings_app_report_title),
                    style = SNUTTTypography.h2,
                )
            },
            navigationIcon = {
                ArrowBackIcon(
                    modifier = Modifier
                        .size(30.dp)
                        .clicks {
                            navController.popBackStack()
                        },
                    colorFilter = ColorFilter.tint(SNUTTColors.Black900),
                )
            },
            actions = {
                SendIcon(
                    modifier = Modifier
                        .size(20.dp)
                        .clicks(throttleMs = 1000L, enabled = sentEnabled) { sendFeedback() },
                    colorFilter = ColorFilter.tint(SNUTTColors.Black900),
                )
            },
        )
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
        ) {
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = stringResource(R.string.settings_app_report_email),
                style = SNUTTTypography.body2.copy(color = SNUTTColors.Black600),
            )
            Spacer(modifier = Modifier.height(10.dp))
            EditText(
                value = email,
                onValueChange = { email = it },
                hint = stringResource(R.string.example_email),
                textStyle = SNUTTTypography.body1.copy(fontSize = 15.sp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Email,
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        focusManager.moveFocus(
                            FocusDirection.Down,
                        )
                    },
                ),
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = stringResource(R.string.settings_app_report_detail),
                style = SNUTTTypography.body2.copy(color = SNUTTColors.Black600),
            )
            Spacer(modifier = Modifier.height(10.dp))
            EditText(
                value = detail,
                onValueChange = { detail = it },
                hint = stringResource(R.string.settings_app_report_detail_hint),
                textStyle = SNUTTTypography.body1.copy(fontSize = 15.sp),
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = stringResource(R.string.settings_app_report_description),
                style = SNUTTTypography.subtitle2.copy(fontSize = 12.sp),
            )
        }
    }
}

@Preview
@Composable
fun AppReportPagePreview() {
    AppReportPage()
}
