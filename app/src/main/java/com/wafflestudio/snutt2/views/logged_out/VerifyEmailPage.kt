package com.wafflestudio.snutt2.views.logged_out

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.EditTextWithTitle
import com.wafflestudio.snutt2.components.compose.SimpleTopBar
import com.wafflestudio.snutt2.components.compose.WebViewStyleButton
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.views.LocalNavController
import com.wafflestudio.snutt2.views.NavigationDestination
import com.wafflestudio.snutt2.views.navigateAsOrigin

@Composable
fun VerifyEmailPage() {
    val navController = LocalNavController.current
//    val userViewModel = hiltViewModel<UserViewModel>()

//    val userEmail = userViewModel.userInfo.collectAsState().value?.email ?: ""
    val userEmail = "eastshine@snu.ac.kr"

    var onInputPage by remember { mutableStateOf(false) }
    var codeField by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SNUTTColors.White900)
    ) {
        SimpleTopBar(
            title = stringResource(R.string.verify_email_app_bar_title),
            onClickNavigateBack = { /*navController.popBackStack()*/ }
        )
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            when (onInputPage) {
                false -> {
                    Text(
                        text = stringResource(R.string.verify_email_question_text, userEmail),
                        style = SNUTTTypography.h2
                    )
                    Spacer(
                        modifier = Modifier
                            .size(30.dp)
                    )
                    Text(
                        text = stringResource(R.string.verify_email_detail_text),
                        style = SNUTTTypography.subtitle1.copy(color = SNUTTColors.Black900)
                    )
                    Spacer(
                        modifier = Modifier
                            .size(100.dp)
                    )
                    WebViewStyleButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onInputPage = true }
                    ) {
                        Text(
                            text = stringResource(R.string.verify_email_ok_button),
                            style = SNUTTTypography.button.copy(color = SNUTTColors.AllWhite)
                        )
                    }
                    Spacer(
                        modifier = Modifier
                            .size(20.dp)
                    )
                    WebViewStyleButton(
                        modifier = Modifier.fillMaxWidth(),
                        color = SNUTTColors.Gray200,
                        onClick = { navController.navigateAsOrigin(NavigationDestination.Home) }
                    ) {
                        Text(
                            text = stringResource(R.string.verify_email_later_button),
                            style = SNUTTTypography.button.copy(color = SNUTTColors.AllWhite)
                        )
                    }
                }
                true -> {
                    Text(
                        text = stringResource(R.string.verify_email_input_code, userEmail),
                        style = SNUTTTypography.h2
                    )
                    Spacer(
                        modifier = Modifier
                            .size(30.dp)
                    )
                    EditTextWithTitle(
                        title = stringResource(R.string.verify_email_code_title, userEmail),
                        titleStyle = SNUTTTypography.h4,
                        value = codeField,
                        onValueChange = { codeField = it },
                        hint = stringResource(R.string.verify_email_code_hint),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { }),
                        singleLine = true,
                        trailingIcon = {
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(
                        modifier = Modifier
                            .size(30.dp)
                    )
                    WebViewStyleButton(
                        modifier = Modifier.fillMaxWidth(),
                        color = SNUTTColors.SNUTTTheme,
                        onClick = { }
                    ) {
                        Text(
                            text = stringResource(R.string.verify_email_ok_button),
                            style = SNUTTTypography.button.copy(color = SNUTTColors.AllWhite)
                        )
                    }
                }
            }
        }
    }
}

@Preview(device = Devices.PIXEL_XL)
@Composable
fun VerifyEmailPagePreview() {
    VerifyEmailPage()
}
