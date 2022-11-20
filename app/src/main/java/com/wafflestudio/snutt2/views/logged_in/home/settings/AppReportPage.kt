package com.wafflestudio.snutt2.views.logged_in.home.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.*
import com.wafflestudio.snutt2.lib.android.toast
import com.wafflestudio.snutt2.lib.data.SNUTTStringUtils.isEmailInvalid
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.views.LocalNavController
import kotlinx.coroutines.launch

@Composable
fun AppReportPage() {
    val context = LocalContext.current
    val navController = LocalNavController.current
    val scope = rememberCoroutineScope()
    val userViewModel = hiltViewModel<UserViewModel>()

    var email by remember { mutableStateOf("") }
    var detail by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        userViewModel.fetchUserInfo()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopBar(title = {
            Text(
                text = stringResource(R.string.settings_app_report_title),
                style = SNUTTTypography.h2,
            )
        }, navigationIcon = {
            ArrowBackIcon(
                modifier = Modifier
                    .size(30.dp)
                    .clicks {
                        navController.popBackStack()
                    },
                colorFilter = ColorFilter.tint(SNUTTColors.Black900),
            )
        }, actions = {
            SendIcon(
                modifier = Modifier
                    .padding(6.dp)
                    .clicks {
                        if (detail.isEmpty()) {
                            context.toast(context.getString(R.string.feedback_empty_detail_warning))
                        } else if (email.isEmailInvalid()) {
                            context.toast(context.getString(R.string.feedback_invalid_email_warning))
                        } else {
                            scope.launch {
                                userViewModel.sendFeedback(email, detail)
                            }
                            context.toast(context.getString(R.string.feedback_send_success_message))
                            navController.popBackStack()
                        }
                    }
            )
        })
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = stringResource(R.string.settings_app_report_email),
                style = SNUTTTypography.h4.copy(fontSize = 13.sp, color = SNUTTColors.Black600),
            )
            Spacer(modifier = Modifier.height(10.dp))
            EditText(
                value = email,
                onValueChange = { email = it },
                hint = "example@gmail.com",
                textStyle = SNUTTTypography.body1.copy(fontSize = 17.sp),
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = stringResource(R.string.settings_app_report_detail),
                style = SNUTTTypography.h4.copy(fontSize = 13.sp, color = SNUTTColors.Black600),
            )
            Spacer(modifier = Modifier.height(10.dp))
            EditText(
                value = detail, onValueChange = { detail = it },
                hint = "불편한 점이나 버그를 적어주세요",
                textStyle = SNUTTTypography.body1.copy(fontSize = 17.sp),
            )
        }
    }
}

@Preview
@Composable
fun AppReportPagePreview() {
    AppReportPage()
}
