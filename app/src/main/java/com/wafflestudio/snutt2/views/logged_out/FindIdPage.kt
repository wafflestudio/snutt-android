package com.wafflestudio.snutt2.views.logged_out

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.EditText
import com.wafflestudio.snutt2.components.compose.SimpleTopBar
import com.wafflestudio.snutt2.components.compose.WebViewStyleButton
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.lib.android.toast
import com.wafflestudio.snutt2.lib.data.SNUTTStringUtils.isEmailInvalid
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.views.LocalApiOnError
import com.wafflestudio.snutt2.views.LocalApiOnProgress
import com.wafflestudio.snutt2.views.LocalNavController
import com.wafflestudio.snutt2.views.launchSuspendApi
import com.wafflestudio.snutt2.views.logged_in.home.settings.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun FindIdPage() {
    val navController = LocalNavController.current
    val apiOnError = LocalApiOnError.current
    val apiOnProgress = LocalApiOnProgress.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    val userViewModel = hiltViewModel<UserViewModel>()

    var emailField by remember { mutableStateOf("") }

    val handleSendIdToEmail = {
        coroutineScope.launch {
            if (emailField.isEmpty()) {
                context.toast(context.getString(R.string.settings_user_config_enter_email))
            } else if (emailField.isEmailInvalid()) {
                context.toast(context.getString(R.string.find_id_wrong_email_format))
            } else {
                launchSuspendApi(apiOnProgress, apiOnError) {
                    userViewModel.findIdByEmail(emailField)
                    context.toast(context.getString(R.string.find_id_send_email_success_message).format(emailField))
                    navController.popBackStack()
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SNUTTColors.White900)
            .clicks { focusManager.clearFocus() }
    ) {
        SimpleTopBar(
            title = stringResource(R.string.sign_in_find_id_button),
            onClickNavigateBack = {
                navController.popBackStack()
            }
        )

        Column(modifier = Modifier.padding(horizontal = 25.dp)) {
            Text(
                text = stringResource(R.string.find_id_content),
                style = SNUTTTypography.h3,
                modifier = Modifier.padding(vertical = 25.dp),
            )
            Text(
                text = stringResource(R.string.settings_app_report_email),
                style = SNUTTTypography.h4
            )
            EditText(
                value = emailField,
                onValueChange = { emailField = it },
                hint = stringResource(R.string.settings_user_config_enter_email),
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
            Spacer(modifier = Modifier.height(30.dp))
            WebViewStyleButton(
                modifier = Modifier.fillMaxWidth(),
                color = if (emailField.isEmpty()) SNUTTColors.Gray400 else SNUTTColors.SNUTTTheme,
                onClick = { handleSendIdToEmail() }
            ) {
                Text(
                    text = stringResource(R.string.common_ok),
                    style = SNUTTTypography.h3.copy(color = SNUTTColors.AllWhite)
                )
            }
        }
    }
}
