package com.wafflestudio.snutt2.views.logged_in.home.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.CustomDialog
import com.wafflestudio.snutt2.components.compose.DuplicateIcon
import com.wafflestudio.snutt2.components.compose.EditText
import com.wafflestudio.snutt2.components.compose.SimpleTopBar
import com.wafflestudio.snutt2.lib.android.toast
import com.wafflestudio.snutt2.lib.copyToClipboard
import com.wafflestudio.snutt2.lib.logging.AnalyticsScreen
import com.wafflestudio.snutt2.lib.logging.logImpression
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography

@Composable
fun UserConfigRoute(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
    onNavigateOnboard: () -> Unit,
    onNavigateChangeNickname: () -> Unit,
    onNavigateSocialLink: () -> Unit,
    viewModel: UserConfigViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val uiState by viewModel.userConfigUiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.userConfigUiEvent.collect { uiEvent ->
            when (uiEvent) {
                is UserConfigUiEvent.ShowToast -> {
                    val message = uiEvent.message
                    if (message.isNotEmpty()) {
                        context.toast(message)
                    }
                }

                is UserConfigUiEvent.ShowToastByEvent -> {
                    val message = when (uiEvent.event) {
                        UserConfigEvent.InvalidIdError -> context.getString(R.string.error_invalid_id)
                        UserConfigEvent.InvalidPasswordError -> context.getString(R.string.error_invalid_password)
                        UserConfigEvent.PasswordMismatchError -> context.getString(R.string.settings_user_config_password_confirm_fail)
                        UserConfigEvent.ChangePasswordSuccess -> context.getString(R.string.settings_user_config_change_password_success)
                        UserConfigEvent.AddIdPasswordSuccess -> context.getString(R.string.settings_user_config_add_local_id_success)
                    }
                    context.toast(message)
                }

                is UserConfigUiEvent.NavigateToOnboard -> {
                    onNavigateOnboard()
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.resetToastMessage()
        }
    }

    UserConfigScreen(
        modifier = modifier,
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onNavigateChangeNickname = onNavigateChangeNickname,
        onCopyNicknameToClipboard = {
            copyToClipboard(
                context = context,
                content = uiState.userName,
                toastMessage = context.getString(R.string.settings_user_nickname_copied_toast),
            )
        },
        onClickChangePassword = viewModel::showChangePasswordDialog,
        onConfirmChangePassword = viewModel::changePassword,
        onDismissChangePassword = viewModel::hideChangePasswordDialog,
        onClickAddIdPassword = viewModel::showAddIdPasswordDialog,
        onConfirmAddIdPassword = viewModel::addNewLocalId,
        onDismissAddIdPassword = viewModel::hideAddIdPasswordDialog,
        onClickLeave = viewModel::showLeaveDialog,
        onConfirmLeave = viewModel::leave,
        onDismissLeave = viewModel::hideLeaveDialog,
        onNavigateSocialLink = onNavigateSocialLink,
    )
}

@Composable
fun UserConfigScreen(
    modifier: Modifier = Modifier,
    uiState: UserConfigUiState,
    onNavigateBack: () -> Unit,
    onNavigateChangeNickname: () -> Unit,
    onCopyNicknameToClipboard: () -> Unit,
    onClickChangePassword: () -> Unit,
    onConfirmChangePassword: (String, String, String) -> Unit,
    onDismissChangePassword: () -> Unit,
    onClickAddIdPassword: () -> Unit,
    onConfirmAddIdPassword: (String, String, String) -> Unit,
    onDismissAddIdPassword: () -> Unit,
    onClickLeave: () -> Unit,
    onConfirmLeave: () -> Unit,
    onDismissLeave: () -> Unit,
    onNavigateSocialLink: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SNUTTColors.SettingBackground)
            .logImpression(AnalyticsScreen.SettingsAccount),
    ) {
        SimpleTopBar(
            title = stringResource(R.string.user_settings_app_bar_title),
            onClickNavigateBack = onNavigateBack,
        )

        Column(
            modifier = Modifier
                .padding(vertical = 10.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            SettingColumn {
                SettingItem(
                    title = stringResource(R.string.settings_user_config_change_nickname),
                    onClick = onNavigateChangeNickname,
                ) {
                    Text(
                        text = uiState.userName,
                        style = SNUTTTypography.body1.copy(
                            color = SNUTTColors.Black500,
                        ),
                    )
                }
                SettingItem(
                    title = stringResource(R.string.settings_user_config_copy_nickname),
                    hasNextPage = false,
                    onClick = onCopyNicknameToClipboard,
                ) {
                    DuplicateIcon(
                        modifier = Modifier.size(30.dp),
                        colorFilter = ColorFilter.tint(SNUTTColors.Black500),
                    )
                }
            }
            SettingColumn {
                if (uiState.localId.isNullOrEmpty().not()) {
                    SettingItem(
                        title = stringResource(R.string.settings_user_config_id),
                        hasNextPage = false,
                    ) {
                        Text(
                            text = uiState.localId.toString(),
                            style = SNUTTTypography.body1.copy(
                                color = SNUTTColors.Black500,
                            ),
                        )
                    }
                    SettingItem(
                        title = stringResource(R.string.settings_user_config_change_password),
                        onClick = onClickChangePassword,
                    )
                } else {
                    SettingItem(
                        title = stringResource(R.string.settings_user_config_add_local_id),
                        onClick = onClickAddIdPassword,
                    )
                }
            }
            SettingItem(
                title = stringResource(R.string.social_link_title),
                onClick = onNavigateSocialLink,
            )
            SettingItem(
                title = stringResource(R.string.settings_app_report_email),
                hasNextPage = false,
            ) {
                Text(
                    text = uiState.email ?: "",
                    style = SNUTTTypography.body1.copy(
                        color = SNUTTColors.Black500,
                    ),
                )
            }
            SettingItem(
                title = stringResource(R.string.settings_user_config_leave),
                titleColor = SNUTTColors.Red,
                onClick = onClickLeave,
            )
        }
    }

    if (uiState.showChangePasswordDialog) {
        var currentPassword by remember { mutableStateOf("") }
        var newPassword by remember { mutableStateOf("") }
        var newPasswordConfirm by remember { mutableStateOf("") }

        CustomDialog(
            onDismiss = onDismissChangePassword,
            onConfirm = { onConfirmChangePassword(currentPassword, newPassword, newPasswordConfirm) },
            title = stringResource(R.string.settings_user_config_change_password),
            positiveButtonText = stringResource(
                R.string.notifications_noti_change,
            ),
        ) {
            val focusManager = LocalFocusManager.current
            Column {
                EditText(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    textStyle = SNUTTTypography.body1.copy(fontSize = 16.sp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                    visualTransformation = PasswordVisualTransformation(),
                    hint = stringResource(R.string.settings_user_config_current_password_hint),
                )
                Spacer(modifier = Modifier.height(25.dp))
                EditText(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    textStyle = SNUTTTypography.body1.copy(fontSize = 16.sp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                    visualTransformation = PasswordVisualTransformation(),
                    hint = stringResource(R.string.settings_user_config_new_password_hint),
                )
                Spacer(modifier = Modifier.height(25.dp))
                EditText(
                    value = newPasswordConfirm,
                    onValueChange = { newPasswordConfirm = it },
                    textStyle = SNUTTTypography.body1.copy(fontSize = 16.sp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { onConfirmChangePassword(currentPassword, newPassword, newPasswordConfirm) }),
                    visualTransformation = PasswordVisualTransformation(),
                    hint = stringResource(R.string.settings_user_config_new_password_confirm_hint),
                )
            }
        }
    }

    if (uiState.showAddIdPasswordDialog) {
        var id by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var passwordConfirm by remember { mutableStateOf("") }

        CustomDialog(
            onDismiss = onDismissAddIdPassword,
            onConfirm = { onConfirmAddIdPassword(id, password, passwordConfirm) },
            title = stringResource(R.string.settings_user_config_add_local_id),
            positiveButtonText = stringResource(
                R.string.notifications_noti_add,
            ),
        ) {
            val focusManager = LocalFocusManager.current
            Column {
                EditText(
                    value = id,
                    onValueChange = { id = it },
                    textStyle = SNUTTTypography.body1.copy(fontSize = 16.sp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                    hint = stringResource(R.string.sign_in_id_hint),
                )
                Spacer(modifier = Modifier.height(25.dp))
                EditText(
                    value = password,
                    onValueChange = { password = it },
                    textStyle = SNUTTTypography.body1.copy(fontSize = 16.sp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                    visualTransformation = PasswordVisualTransformation(),
                    hint = stringResource(R.string.sign_in_password_hint),
                )
                Spacer(modifier = Modifier.height(25.dp))
                EditText(
                    value = passwordConfirm,
                    onValueChange = { passwordConfirm = it },
                    textStyle = SNUTTTypography.body1.copy(fontSize = 16.sp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onNext = { onConfirmAddIdPassword(id, password, passwordConfirm) }),
                    visualTransformation = PasswordVisualTransformation(),
                    hint = stringResource(R.string.sign_up_password_confirm_hint),
                )
            }
        }
    }

    if (uiState.showLeaveDialog) {
        CustomDialog(
            onDismiss = onDismissLeave,
            onConfirm = onConfirmLeave,
            title = stringResource(R.string.settings_user_config_leave),
            positiveButtonText = stringResource(R.string.settings_user_config_leave),
        ) {
            Text(text = stringResource(R.string.settings_leave_message), style = SNUTTTypography.body2)
        }
    }
}

@Preview
@Composable
fun UserConfigPagePreview() {
    UserConfigScreen(
        uiState = UserConfigUiState(
            "이현도", "lhd", "lhd@email.com", false, false, false,
        ),
        onNavigateBack = {},
        onNavigateChangeNickname = {},
        onCopyNicknameToClipboard = {},
        onClickChangePassword = {},
        onConfirmChangePassword = { _, _, _ -> },
        onDismissChangePassword = {},
        onClickAddIdPassword = {},
        onConfirmAddIdPassword = { _, _, _ -> },
        onDismissAddIdPassword = {},
        onClickLeave = {},
        onConfirmLeave = {},
        onDismissLeave = {},
        onNavigateSocialLink = {},
    )
}
