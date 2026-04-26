package com.wafflestudio.snutt2.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.ui.components.compose.ArrowBackIcon
import com.wafflestudio.snutt2.ui.components.compose.CloseCircleIcon
import com.wafflestudio.snutt2.ui.components.compose.EditText
import com.wafflestudio.snutt2.ui.components.compose.TopBar
import com.wafflestudio.snutt2.ui.components.compose.clearFocusOnKeyboardDismiss
import com.wafflestudio.snutt2.ui.components.compose.clicks
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewSurface
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography
import com.wafflestudio.snutt2.ui.util.toDp
import com.wafflestudio.snutt2.ui.util.toast

@Composable
fun ChangeNicknamePage(
    viewModel: ChangeNicknameViewModel = hiltViewModel<ChangeNicknameViewModel>(),
    onNavigateBack: () -> Unit,
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is ChangeNicknameUiEvent.ShowToast -> context.toast(event.message)
                is ChangeNicknameUiEvent.Success -> onNavigateBack()
            }
        }
    }

    ChangeNicknameScreen(
        initialNickname = viewModel.initialNickname,
        nicknameField = uiState.nicknameField,
        onNicknameFieldChange = viewModel::onNicknameFieldChange,
        onSave = viewModel::changeNickname,
        onNavigateBack = onNavigateBack,
    )
}

@Composable
private fun ChangeNicknameScreen(
    initialNickname: String,
    nicknameField: String,
    onNicknameFieldChange: (String) -> Unit,
    onSave: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    val nicknameRequirementTexts = listOf(
        stringResource(R.string.settings_change_nickname_requirement_0),
        stringResource(R.string.settings_change_nickname_requirement_1),
        stringResource(R.string.settings_change_nickname_requirement_2),
    )

    val canSave = nicknameField.isNotEmpty() && nicknameField != initialNickname
    val handleSave = { if (canSave) onSave() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SNUTTColors.SettingBackground),
    ) {
        TopBar(
            title = {
                Text(
                    text = stringResource(R.string.settings_change_nickname_app_bar_title),
                    style = SNUTTTypography.h2,
                )
            },
            navigationIcon = {
                ArrowBackIcon(
                    modifier = Modifier
                        .size(30.dp)
                        .clicks { onNavigateBack() },
                    colorFilter = ColorFilter.tint(SNUTTColors.Black900),
                )
            },
            actions = {
                Text(
                    text = stringResource(R.string.settings_change_nickname_app_bar_save),
                    style = SNUTTTypography.body1,
                    color = if (canSave) SNUTTColors.Black900 else SNUTTColors.Black500,
                    modifier = Modifier.clicks { handleSave() },
                )
            },
        )
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
        ) {
            SettingColumn(
                title = stringResource(R.string.settings_change_nickname_title),
            ) {
                NicknameEditText(
                    value = nicknameField,
                    onValueChange = onNicknameFieldChange,
                    onDone = { handleSave() },
                    hint = initialNickname,
                )
            }
            Spacer(Modifier.height(12.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp),
            ) {
                Text(
                    text = stringResource(R.string.settings_change_nickname_guide),
                    style = SNUTTTypography.body2.copy(color = SNUTTColors.Black500),
                )
                Spacer(Modifier.height(30.dp))
                Column(verticalArrangement = Arrangement.spacedBy(2.sp.toDp())) {
                    Text(
                        text = stringResource(R.string.settings_change_nickname_requirement_title),
                        style = SNUTTTypography.h5.copy(color = SNUTTColors.Black500),
                    )
                    nicknameRequirementTexts.forEach {
                        BulletedParagraph(
                            text = it,
                            style = SNUTTTypography.body2.copy(color = SNUTTColors.Black500),
                        )
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
        }
    }
}

@Composable
private fun NicknameEditText(
    value: String,
    onValueChange: (String) -> Unit,
    onDone: (KeyboardActionScope.() -> Unit),
    hint: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(45.dp)
            .background(SNUTTColors.White900)
            .padding(horizontal = 35.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val keyboardController = LocalSoftwareKeyboardController.current
        var isFocused by remember { mutableStateOf(false) }
        EditText(
            modifier = Modifier
                .weight(1f)
                .onFocusChanged { isFocused = it.isFocused }
                .clearFocusOnKeyboardDismiss(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = onDone),
            singleLine = true,
            value = value,
            onValueChange = onValueChange,
            hint = hint,
            underlineEnabled = false,
            textStyle = SNUTTTypography.body1.copy(fontSize = 16.sp),
        )
        if (isFocused && value.isNotEmpty()) {
            CloseCircleIcon(
                modifier = Modifier
                    .size(30.dp)
                    .clicks {
                        onValueChange("")
                        keyboardController?.hide()
                    },
            )
        }
        Text(
            text = "#NNNN",
            style = SNUTTTypography.body1.copy(color = SNUTTColors.Black300, fontSize = 16.sp),
        )
    }
}

@Composable
private fun BulletedParagraph(
    text: String,
    style: TextStyle,
) {
    Text(
        text = buildAnnotatedString {
            withStyle(ParagraphStyle(textIndent = TextIndent(restLine = style.fontSize))) {
                append("\u2022")
                append("\t\t")
                append(text)
            }
        },
        style = style,
    )
}

@SnuttPreview
@Composable
private fun ChangeNicknameScreen_Default() {
    SnuttPreviewSurface {
        ChangeNicknameScreen(
            initialNickname = "와플",
            nicknameField = "와플",
            onNicknameFieldChange = {},
            onSave = {},
            onNavigateBack = {},
        )
    }
}
