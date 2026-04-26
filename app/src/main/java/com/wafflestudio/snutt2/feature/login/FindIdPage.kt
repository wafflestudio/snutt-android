package com.wafflestudio.snutt2.feature.login

import androidx.compose.foundation.background
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.lib.isEmailInvalid
import com.wafflestudio.snutt2.ui.components.compose.EditText
import com.wafflestudio.snutt2.ui.components.compose.SimpleTopBar
import com.wafflestudio.snutt2.ui.components.compose.WebViewStyleButton
import com.wafflestudio.snutt2.ui.components.compose.clicks
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewSurface
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography
import com.wafflestudio.snutt2.ui.util.toast

@Composable
fun FindIdPage(
    viewModel: FindIdViewModel = hiltViewModel<FindIdViewModel>(),
    onNavigateBack: () -> Unit,
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val successMessageTemplate = stringResource(R.string.find_id_send_email_success_message)

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is FindIdUiEvent.ShowToast -> context.toast(event.message)
                is FindIdUiEvent.Success -> {
                    context.toast(successMessageTemplate.format(event.email))
                    onNavigateBack()
                }
            }
        }
    }

    FindIdScreen(
        emailField = uiState.emailField,
        onEmailFieldChange = viewModel::onEmailFieldChange,
        onSubmit = viewModel::findIdByEmail,
        onNavigateBack = onNavigateBack,
    )
}

@Composable
private fun FindIdScreen(
    emailField: String,
    onEmailFieldChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val enterEmailMessage = stringResource(R.string.settings_user_config_enter_email)
    val wrongEmailFormatMessage = stringResource(R.string.find_id_wrong_email_format)

    val buttonEnabled = emailField.isNotEmpty()

    val handleSendIdToEmail = {
        if (emailField.isEmpty()) {
            context.toast(enterEmailMessage)
        } else if (emailField.isEmailInvalid()) {
            context.toast(wrongEmailFormatMessage)
        } else {
            onSubmit()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SNUTTColors.White900)
            .clicks { focusManager.clearFocus() },
    ) {
        SimpleTopBar(
            title = stringResource(R.string.sign_in_find_id_button),
            onClickNavigateBack = onNavigateBack,
        )

        Column(modifier = Modifier.padding(horizontal = 25.dp)) {
            Text(
                text = stringResource(R.string.find_id_content),
                style = SNUTTTypography.h3,
                modifier = Modifier.padding(vertical = 25.dp),
            )
            Text(
                text = stringResource(R.string.settings_app_report_email),
                style = SNUTTTypography.h4,
            )
            EditText(
                value = emailField,
                onValueChange = onEmailFieldChange,
                hint = stringResource(R.string.settings_user_config_enter_email),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) },
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
            )
            Spacer(modifier = Modifier.height(30.dp))
            WebViewStyleButton(
                modifier = Modifier.fillMaxWidth(),
                enabled = buttonEnabled,
                onClick = { handleSendIdToEmail() },
            ) {
                Text(
                    text = stringResource(R.string.common_ok),
                    style = SNUTTTypography.h3.copy(color = SNUTTColors.AllWhite),
                )
            }
        }
    }
}

@SnuttPreview
@Composable
private fun FindIdScreen_Default() {
    SnuttPreviewSurface {
        FindIdScreen(
            emailField = "",
            onEmailFieldChange = {},
            onSubmit = {},
            onNavigateBack = {},
        )
    }
}
