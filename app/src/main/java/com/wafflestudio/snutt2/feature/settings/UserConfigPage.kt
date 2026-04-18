package com.wafflestudio.snutt2.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.logging.AnalyticsScreen
import com.wafflestudio.snutt2.logging.compose.logImpression
import com.wafflestudio.snutt2.ui.components.compose.DuplicateIcon
import com.wafflestudio.snutt2.ui.components.compose.SimpleTopBar
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography
import com.wafflestudio.snutt2.ui.util.copyToClipboard
import com.wafflestudio.snutt2.ui.util.toast

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
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val invalidIdError = stringResource(R.string.error_invalid_id)
    val invalidPasswordError = stringResource(R.string.error_invalid_password)
    val passwordMismatchError = stringResource(R.string.settings_user_config_password_confirm_fail)
    val changePasswordSuccess = stringResource(R.string.settings_user_config_change_password_success)
    val addIdPasswordSuccess = stringResource(R.string.settings_user_config_add_local_id_success)
    val nicknameCopiedToast = stringResource(R.string.settings_user_nickname_copied_toast)

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { uiEvent ->
            when (uiEvent) {
                is UserConfigUiEvent.ShowToast -> {
                    val message = uiEvent.message
                    if (message.isNotEmpty()) {
                        context.toast(message)
                    }
                }

                is UserConfigUiEvent.ShowToastByEvent -> {
                    val message = when (uiEvent.event) {
                        UserConfigEvent.InvalidIdError -> invalidIdError
                        UserConfigEvent.InvalidPasswordError -> invalidPasswordError
                        UserConfigEvent.PasswordMismatchError -> passwordMismatchError
                        UserConfigEvent.ChangePasswordSuccess -> changePasswordSuccess
                        UserConfigEvent.AddIdPasswordSuccess -> addIdPasswordSuccess
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
                toastMessage = nicknameCopiedToast,
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
                            text = uiState.localId,
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

    UserConfigDialogs(
        dialogState = uiState.dialogState,
        onConfirmChangePassword = onConfirmChangePassword,
        onDismissChangePassword = onDismissChangePassword,
        onConfirmAddIdPassword = onConfirmAddIdPassword,
        onDismissAddIdPassword = onDismissAddIdPassword,
        onConfirmLeave = onConfirmLeave,
        onDismissLeave = onDismissLeave,
    )
}

@Preview
@Composable
fun UserConfigPagePreview() {
    UserConfigScreen(
        uiState = UserConfigUiState(
            userName = "이현도",
            localId = "lhd",
            email = "lhd@email.com",
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
