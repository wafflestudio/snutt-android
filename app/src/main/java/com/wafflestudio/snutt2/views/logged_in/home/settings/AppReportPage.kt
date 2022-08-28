package com.wafflestudio.snutt2.views.logged_in.home.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.ArrowBackIcon
import com.wafflestudio.snutt2.components.compose.EditText
import com.wafflestudio.snutt2.components.compose.SendIcon
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.views.NavControllerContext
import kotlinx.coroutines.launch

@Composable
fun AppReportPage() {
    val context = LocalContext.current
    val navController = NavControllerContext.current
    val scope = rememberCoroutineScope()
    val userViewModel = hiltViewModel<UserViewModel>()

    var email by remember { mutableStateOf("") }
    var detail by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(text = stringResource(R.string.settings_app_report_title)) },
            navigationIcon = {
                ArrowBackIcon(
                    modifier = Modifier
                        .size(30.dp)
                        .clicks {
                            navController.popBackStack()
                        }
                )
            },
            actions = {
                SendIcon(
                    modifier = Modifier.clicks {
                        scope.launch { userViewModel.sendFeedback(email, detail) }
                        navController.popBackStack()
                    }
                )
            }
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = stringResource(R.string.settings_app_report_email))
        Spacer(modifier = Modifier.height(10.dp))
        EditText(value = email, onValueChange = { email = it })
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = stringResource(R.string.settings_app_report_detail))
        Spacer(modifier = Modifier.height(10.dp))
        EditText(value = detail, onValueChange = { detail = it })
    }
}

@Preview
@Composable
fun AppReportPagePreview() {
    AppReportPage()
}
