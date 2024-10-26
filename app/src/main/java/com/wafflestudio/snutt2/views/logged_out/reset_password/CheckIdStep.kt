package com.wafflestudio.snutt2.views.logged_out.reset_password

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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.EditTextFieldValue
import com.wafflestudio.snutt2.components.compose.WebViewStyleButton
import com.wafflestudio.snutt2.lib.android.toast
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography

@Composable
fun CheckIdStep(
    userId: String,
    onSubmit: (String) -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    val context = LocalContext.current

    var idField by remember {
        mutableStateOf(
            TextFieldValue(
                text = userId,
                selection = TextRange(userId.length), // 초기 커서를 텍스트 끝으로 설정
            ),
        )
    }
    val buttonEnabled by remember {
        derivedStateOf {
            idField.text.isNotEmpty()
        }
    }

    val sendIdAndRequestMaskedEmail = {
        if (idField.text.isEmpty()) {
            context.toast(context.getString(R.string.find_password_enter_id_hint))
        } else {
            onSubmit(idField.text)
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
            text = stringResource(R.string.find_password_check_email_content),
            style = SNUTTTypography.h2.copy(fontSize = 17.sp),
        )

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = stringResource(R.string.sign_in_id_title),
            style = SNUTTTypography.body1.copy(color = SNUTTColors.EditTextLabel),
        )

        Spacer(modifier = Modifier.height(12.dp))

        EditTextFieldValue(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .onFocusChanged {
                    idField = idField.copy(selection = TextRange(idField.text.length))
                },
            value = idField,
            onValueChange = { idField = it },
            hint = stringResource(R.string.find_password_enter_id_hint),
            keyboardActions = KeyboardActions(
                onDone = {
                    onSubmit(idField.text)
                },
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            singleLine = true,
            underlineColorFocused = if (buttonEnabled) SNUTTColors.SNUTTTheme else SNUTTColors.EditTextUnderline,
        )

        Spacer(modifier = Modifier.height(48.dp))

        WebViewStyleButton(
            modifier = Modifier.fillMaxWidth(),
            enabled = buttonEnabled,
            onClick = sendIdAndRequestMaskedEmail,
        ) {
            Text(
                text = stringResource(R.string.common_ok),
                style = SNUTTTypography.h3.copy(color = if (buttonEnabled) SNUTTColors.AllWhite else SNUTTColors.VacancyGray),
            )
        }
    }
}
