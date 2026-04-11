package com.wafflestudio.snutt2.feature.login.reset_password

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.ui.components.compose.EditText
import com.wafflestudio.snutt2.ui.components.compose.EditTextFieldValue
import com.wafflestudio.snutt2.ui.components.compose.WebViewStyleButton
import com.wafflestudio.snutt2.ui.components.compose.clicks
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography
import com.wafflestudio.snutt2.ui.util.isEmailInvalid
import com.wafflestudio.snutt2.ui.util.toast

@Composable
fun EnterFullEmailStep(
    uiState: FindPasswordViewModel.UIState.EnterFullEmail,
    notMyEmail: () -> Unit,
    onSubmitFullEmail: (String) -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    val context = LocalContext.current

    var emailField by remember {
        mutableStateOf(
            TextFieldValue(
                text = uiState.fullEmail,
                selection = TextRange(uiState.fullEmail.length), // 초기 커서를 텍스트 끝으로 설정
            ),
        )
    }
    val buttonEnabled by remember {
        derivedStateOf {
            !emailField.text.isEmailInvalid()
        }
    }

    val sendIdAndRequestMaskedEmail: () -> Unit = {
        if (buttonEnabled) {
            if (emailField.text.isEmpty()) {
                context.toast(context.getString(R.string.find_password_enter_id_hint))
            } else {
                onSubmitFullEmail(emailField.text)
            }
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 44.dp, horizontal = 20.dp),
    ) {
        Text(
            text = stringResource(R.string.find_password_check_email_enter_full_email),
            style = SNUTTTypography.h3,
        )

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = stringResource(R.string.sign_in_id_title),
            style = SNUTTTypography.body1.copy(color = SNUTTColors.EditTextLabel),
        )

        Spacer(modifier = Modifier.height(12.dp))

        EditText(
            modifier = Modifier.fillMaxWidth(),
            value = "",
            onValueChange = {},
            hint = uiState.userId,
            enabled = false,
        )

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = stringResource(R.string.find_password_check_email_enter_full_email_label),
            style = SNUTTTypography.body1.copy(color = SNUTTColors.EditTextLabel),
        )

        Spacer(modifier = Modifier.height(12.dp))

        EditTextFieldValue(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            value = emailField,
            onValueChange = { emailField = it },
            hint = stringResource(R.string.find_password_check_email_enter_full_email_hint),
            keyboardActions = KeyboardActions(
                onDone = {
                    sendIdAndRequestMaskedEmail()
                },
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            singleLine = true,
            underlineColorFocused = if (emailField.text.isBlank()) SNUTTColors.EditTextUnderline else SNUTTColors.SNUTTTheme,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = uiState.maskedEmail,
            style = SNUTTTypography.body1.copy(color = SNUTTColors.EditTextLabel),
        )

        Spacer(modifier = Modifier.height(48.dp))

        WebViewStyleButton(
            modifier = Modifier.fillMaxWidth(),
            enabled = buttonEnabled,
            onClick = sendIdAndRequestMaskedEmail,
        ) {
            Text(
                text = stringResource(R.string.find_password_check_email_enter_full_email_enter),
                style = SNUTTTypography.h3.copy(color = if (buttonEnabled) SNUTTColors.AllWhite else SNUTTColors.VacancyGray),
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text(
                text = stringResource(R.string.find_password_check_email_enter_full_email_not_mine),
                style = SNUTTTypography.body1.copy(color = SNUTTColors.VacancyGray),
                modifier = Modifier.clicks {
                    notMyEmail()
                },
            )
        }
    }
}
