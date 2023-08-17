package com.wafflestudio.snutt2.views.logged_in.home.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.*
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.views.*
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.Margin
import kotlinx.coroutines.launch

@Composable
fun ChangeNicknamePage() {
    val navController = LocalNavController.current
    val apiOnProgress = LocalApiOnProgress.current
    val apiOnError = LocalApiOnError.current
    val scope = rememberCoroutineScope()
    val userViewModel = hiltViewModel<UserViewModel>()

    val user by userViewModel.userInfo.collectAsState()
    val initialNickname = user?.nickname?.nickname ?: ""
    var nicknameField by remember { mutableStateOf(user?.nickname?.nickname ?: "") }
    val nicknameRequirementTexts = listOf(
        stringResource(R.string.settings_change_nickname_requirement_0),
        stringResource(R.string.settings_change_nickname_requirement_1),
        stringResource(R.string.settings_change_nickname_requirement_2),
        stringResource(R.string.settings_change_nickname_requirement_3),
    )

    val onBackPressed = {
        if (navController.currentDestination?.route == NavigationDestination.ChangeNickname) {
            navController.popBackStack()
        }
    }

    val handleChangeNickname = {
        scope.launch {
            launchSuspendApi(apiOnProgress, apiOnError) {
                userViewModel.changeNickname(nicknameField)
                onBackPressed()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SNUTTColors.Gray100)
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
                        .clicks { onBackPressed() },
                    colorFilter = ColorFilter.tint(SNUTTColors.Black900)
                )
            },
            actions = {
                Text(
                    text = stringResource(R.string.settings_change_nickname_app_bar_save),
                    style = SNUTTTypography.body1,
                    color = if (nicknameField.isEmpty() || nicknameField == initialNickname) SNUTTColors.Black500 else SNUTTColors.Black900,
                    modifier = Modifier
                        .clicks {
                            if (nicknameField.isNotEmpty() && nicknameField != initialNickname)
                                handleChangeNickname()
                        }
                )
            }
        )
        Margin(10.dp)
        SettingColumn(
            title = stringResource(R.string.settings_change_nickname_title)
        ) {
            NicknameEditText(
                value = nicknameField,
                onValueChange = { nicknameField = it },
                onDone = { handleChangeNickname() },
                hint = initialNickname,
            )
        }
        Margin(12.dp)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
        ) {
            Text(
                text = stringResource(R.string.settings_change_nickname_guide),
                style = SNUTTTypography.body2.copy(
                    color = SNUTTColors.Black500,
                ),
            )
            Margin(30.dp)
            Text(
                text = stringResource(R.string.settings_change_nickname_requirement_title),
                style = SNUTTTypography.h5.copy(
                    color = SNUTTColors.Black500,
                ),
            )
            Text(
                text = buildAnnotatedString {
                    withStyle(ParagraphStyle(textIndent = TextIndent(restLine = 12.sp), lineHeight = 19.2.sp)) {
                        nicknameRequirementTexts.forEach {
                            append("\u2022")
                            append("\t\t")
                            append(it)
                            append("\n")
                        }
                    }
                },
                style = SNUTTTypography.body2.copy(
                    color = SNUTTColors.Black500,
                ),
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun NicknameEditText(
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
        verticalAlignment = Alignment.CenterVertically
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
            value = value,
            onValueChange = onValueChange,
            hint = hint,
            underlineEnabled = false,
            textStyle = SNUTTTypography.body1.copy(
                fontSize = 16.sp,
            )
        )
        if (isFocused) {
            CloseCircleIcon(
                modifier = Modifier
                    .size(30.dp)
                    .clicks {
                        onValueChange("")
                        keyboardController?.hide()
                    }
            )
        }
        Text(
            text = "#NNNN",
            style = SNUTTTypography.body1.copy(
                color = SNUTTColors.Black500,
                fontSize = 16.sp,
            )
        )
    }
}
