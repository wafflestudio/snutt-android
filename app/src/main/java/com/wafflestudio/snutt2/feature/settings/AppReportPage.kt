package com.wafflestudio.snutt2.feature.settings

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.lib.isEmailInvalid
import com.wafflestudio.snutt2.logging.AnalyticsScreen
import com.wafflestudio.snutt2.logging.compose.logImpression
import com.wafflestudio.snutt2.ui.components.compose.EditText
import com.wafflestudio.snutt2.ui.components.compose.SnuttIcon
import com.wafflestudio.snutt2.ui.components.compose.TopBar
import com.wafflestudio.snutt2.ui.components.compose.clicks
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewSurface
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography
import com.wafflestudio.snutt2.ui.util.toast

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AppReportPage(
    viewModel: AppReportViewModel = hiltViewModel<AppReportViewModel>(),
    onNavigateBack: () -> Unit,
) {
    val context = LocalContext.current
    val keyboardManager = LocalSoftwareKeyboardController.current
    val sendSuccessMessage = stringResource(R.string.feedback_send_success_message)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is AppReportUiEvent.ShowToast -> context.toast(event.message)
                is AppReportUiEvent.Success -> {
                    keyboardManager?.hide()
                    context.toast(sendSuccessMessage)
                    onNavigateBack()
                }
            }
        }
    }

    AppReportScreen(
        email = uiState.email,
        detail = uiState.detail,
        sentEnabled = uiState.sentEnabled,
        onEmailChange = viewModel::onEmailChange,
        onDetailChange = viewModel::onDetailChange,
        onSendFeedback = viewModel::sendFeedback,
        onNavigateBack = onNavigateBack,
    )
}

@Composable
private fun AppReportScreen(
    email: String,
    detail: String,
    sentEnabled: Boolean,
    onEmailChange: (String) -> Unit,
    onDetailChange: (String) -> Unit,
    onSendFeedback: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val emptyDetailWarning = stringResource(R.string.feedback_empty_detail_warning)
    val invalidEmailWarning = stringResource(R.string.feedback_invalid_email_warning)

    val sendFeedback = {
        if (detail.isEmpty()) {
            context.toast(emptyDetailWarning)
        } else if (email.isEmailInvalid()) {
            context.toast(invalidEmailWarning)
        } else {
            onSendFeedback()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SNUTTColors.White900)
            .logImpression(AnalyticsScreen.SettingsSupport),
    ) {
        TopBar(
            title = {
                Text(
                    text = stringResource(R.string.settings_app_report_title),
                    style = SNUTTTypography.h2,
                )
            },
            navigationIcon = {
                SnuttIcon(
                    R.drawable.ic_arrow_back,
                    modifier = Modifier
                        .size(30.dp)
                        .clicks { onNavigateBack() },
                    colorFilter = ColorFilter.tint(SNUTTColors.Black900),
                )
            },
            actions = {
                SnuttIcon(
                    R.drawable.ic_send,
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
                onValueChange = onEmailChange,
                hint = stringResource(R.string.example_email),
                textStyle = SNUTTTypography.body1.copy(fontSize = 15.sp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Email,
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) },
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
                onValueChange = onDetailChange,
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

@SnuttPreview
@Composable
private fun AppReportScreen_Default() {
    SnuttPreviewSurface {
        AppReportScreen(
            email = "user@snu.ac.kr",
            detail = "",
            sentEnabled = true,
            onEmailChange = {},
            onDetailChange = {},
            onSendFeedback = {},
            onNavigateBack = {},
        )
    }
}
