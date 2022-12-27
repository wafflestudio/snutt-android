package com.wafflestudio.snutt2.views.logged_out

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.BorderButton
import com.wafflestudio.snutt2.components.compose.EditText
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
    var sendDone by remember { mutableStateOf(false) }

    val handleSendIdToEmail = {
        coroutineScope.launch {
            if (emailField.isEmpty()) {
                context.toast("이메일을 입력해주세요.")
            } else if (emailField.isEmailInvalid()) {
                context.toast("올바른 이메일을 입력해주세요.")
            } else {
                launchSuspendApi(apiOnProgress, apiOnError) {
                    userViewModel.findIdByEmail(emailField)
                    sendDone = true
                }
            }
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxSize()
            .background(SNUTTColors.White900)
            .padding(30.dp)
            .clicks { focusManager.clearFocus() }
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = stringResource(R.string.sign_in_logo_title),
                modifier = Modifier.padding(top = 20.dp, bottom = 15.dp),
            )

            Text(
                text = stringResource(R.string.sign_in_logo_title),
                style = SNUTTTypography.h1,
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(25.dp),
            modifier = Modifier
                .weight(1f)
                .padding(top = 50.dp, bottom = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .background(SNUTTColors.Gray100)
                        .height(1.dp)
                        .weight(1f)
                )
                Text(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    text = "아이디 찾기",
                    style = SNUTTTypography.body2,
                    color = SNUTTColors.Gray200
                )
                Box(
                    modifier = Modifier
                        .background(SNUTTColors.Gray100)
                        .height(1.dp)
                        .weight(1f)
                )
            }

            EditText(
                value = emailField,
                onValueChange = { emailField = it },
                hint = stringResource(R.string.sign_up_email_input_hint),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            BorderButton(
                modifier = Modifier.fillMaxWidth(),
                color = SNUTTColors.Gray200,
                onClick = { handleSendIdToEmail() }
            ) {
                Text(
                    text = "이메일로 아이디 전송", style = SNUTTTypography.button
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .background(SNUTTColors.Gray100)
                        .height(1.dp)
                        .weight(1f)
                )
                if (sendDone) {
                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        text = "전송 완료!",
                        style = SNUTTTypography.body2.copy(color = SNUTTColors.SNUTTTheme),
                        color = SNUTTColors.SNUTTTheme
                    )
                }
                Box(
                    modifier = Modifier
                        .background(SNUTTColors.Gray100)
                        .height(1.dp)
                        .weight(1f)
                )
            }

            BorderButton(
                modifier = Modifier.fillMaxWidth(),
                color = SNUTTColors.Gray100,
                onClick = {
                    if (sendDone) {
                        navController.popBackStack()
                    }
                }
            ) {
                Text(
                    text = "로그인하러 가기",
                    style = SNUTTTypography.button.copy(color = if (sendDone) SNUTTColors.SNUTTTheme else SNUTTColors.Gray200)
                )
            }
        }
    }
}
