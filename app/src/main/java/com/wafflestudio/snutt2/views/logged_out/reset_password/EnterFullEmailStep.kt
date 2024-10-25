package com.wafflestudio.snutt2.views.logged_out.reset_password

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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.EditText
import com.wafflestudio.snutt2.components.compose.WebViewStyleButton
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.lib.android.toast
import com.wafflestudio.snutt2.lib.data.SNUTTStringUtils.isEmailInvalid
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EnterFullEmailStep(
    userId: String,
    maskedEmail: String,
    notMyEmail: () -> Unit,
    onSubmitFullEmail: (String) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    var emailField by remember { mutableStateOf("") }
    val buttonEnabled by remember {
        derivedStateOf {
            !emailField.isEmailInvalid()
        }
    }

    val sendIdAndRequestMaskedEmail = {
        if (emailField.isEmpty()) {
            context.toast(context.getString(R.string.find_password_enter_id_hint))
        } else {
            onSubmitFullEmail(emailField)
        }
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
            hint = userId,
            enabled = false,
        )

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = stringResource(R.string.find_password_check_email_enter_full_email_label),
            style = SNUTTTypography.body1.copy(color = SNUTTColors.EditTextLabel),
        )

        Spacer(modifier = Modifier.height(12.dp))

        EditText(
            modifier = Modifier.fillMaxWidth(),
            value = emailField,
            onValueChange = { emailField = it },
            hint = stringResource(R.string.find_password_check_email_enter_full_email_hint),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(
                        FocusDirection.Enter,
                    )
                },
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            singleLine = true,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = maskedEmail,
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
                style = SNUTTTypography.h3.copy(color = SNUTTColors.VacancyGray),
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
