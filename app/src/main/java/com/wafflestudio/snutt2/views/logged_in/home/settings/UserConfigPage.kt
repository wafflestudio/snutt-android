package com.wafflestudio.snutt2.views.logged_in.home.settings

import android.os.Build
import android.os.Build.VERSION_CODES
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.*
import com.wafflestudio.snutt2.lib.android.toast
import com.wafflestudio.snutt2.lib.network.dto.core.UserDto
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.views.*
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.Margin
import kotlinx.coroutines.launch

@Composable
fun UserConfigPage() {
    val context = LocalContext.current
    val navController = LocalNavController.current
    val scope = rememberCoroutineScope()
    val apiOnProgress = LocalApiOnProgress.current
    val apiOnError = LocalApiOnError.current
    val clipboardManager = LocalClipboardManager.current

    val viewModel = hiltViewModel<UserViewModel>()
    val user: UserDto? by viewModel.userInfo.collectAsState()

    var addIdPasswordDialogState by remember { mutableStateOf(false) }
    var passwordChangeDialogState by remember { mutableStateOf(false) }
    var leaveDialogState by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SNUTTColors.SettingBackground),
    ) {
        SimpleTopBar(
            title = stringResource(R.string.user_settings_app_bar_title),
            onClickNavigateBack = { navController.popBackStack() },
        )
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState()),
        ) {
            Margin(height = 10.dp)
            SettingColumn {
                SettingItem(
                    title = stringResource(R.string.settings_user_config_change_nickname),
                    onClick = {
                        navController.navigate(NavigationDestination.ChangeNickname)
                    },
                ) {
                    Text(
                        text = user?.nickname.toString(),
                        style = SNUTTTypography.body1.copy(
                            color = SNUTTColors.Black500,
                        ),
                    )
                }
                SettingItem(
                    title = stringResource(R.string.settings_user_config_copy_nickname),
                    hasNextPage = false,
                    onClick = {
                        clipboardManager.setText(AnnotatedString(user?.nickname.toString()))
                        if (Build.VERSION.SDK_INT <= VERSION_CODES.S_V2) {
                            context.toast(context.getString(R.string.settings_user_nickname_copied_toast))
                        }
                    },
                ) {
                    DuplicateIcon(
                        modifier = Modifier.size(30.dp),
                        colorFilter = ColorFilter.tint(SNUTTColors.Black500),
                    )
                }
            }
            Margin(height = 10.dp)
            SettingColumn {
                if (user?.localId.isNullOrEmpty().not()) {
                    SettingItem(
                        title = stringResource(R.string.settings_user_config_id),
                        hasNextPage = false,
                    ) {
                        Text(
                            text = user?.localId.toString(),
                            style = SNUTTTypography.body1.copy(
                                color = SNUTTColors.Black500,
                            ),
                        )
                    }
                    SettingItem(
                        title = stringResource(R.string.settings_user_config_change_password),
                        onClick = { passwordChangeDialogState = true },
                    )
                } else {
                    SettingItem(
                        title = stringResource(R.string.settings_user_config_add_local_id),
                        onClick = { addIdPasswordDialogState = true },
                    )
                }
            }
            Margin(height = 10.dp)
            SettingItem(
                title = stringResource(R.string.social_link_title),
                onClick = { navController.navigate(NavigationDestination.SocialLink) },
            )
            Margin(height = 10.dp)
            SettingItem(
                title = stringResource(R.string.settings_app_report_email),
                hasNextPage = false,
            ) {
                Text(
                    text = user?.email ?: "",
                    style = SNUTTTypography.body1.copy(
                        color = SNUTTColors.Black500,
                    ),
                )
            }
            Margin(height = 10.dp)
            SettingItem(
                title = stringResource(R.string.settings_user_config_leave),
                titleColor = SNUTTColors.Red,
                onClick = { leaveDialogState = true },
            )
            Margin(height = 10.dp)
        }
    }

    if (addIdPasswordDialogState) {
        var id by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var passwordConfirm by remember { mutableStateOf("") }

        CustomDialog(
            onDismiss = { addIdPasswordDialogState = false },
            onConfirm = {
                if (password != passwordConfirm) {
                    context.toast(context.getString(R.string.settings_user_config_password_confirm_fail))
                } else {
                    scope.launch {
                        launchSuspendApi(apiOnProgress, apiOnError) {
                            viewModel.addNewLocalId(id, password)
                            context.toast(context.getString(R.string.settings_user_config_add_local_id_success))
                            viewModel.fetchUserInfo()
                            addIdPasswordDialogState = false
                        }
                    }
                }
            },
            title = stringResource(R.string.settings_user_config_add_local_id),
            positiveButtonText = stringResource(
                R.string.notifications_noti_add,
            ),
        ) {
            Column {
                EditText(
                    value = id,
                    onValueChange = { id = it },
                    textStyle = SNUTTTypography.body1.copy(fontSize = 16.sp),
                    hint = stringResource(R.string.sign_in_id_hint),
                )
                Spacer(modifier = Modifier.height(25.dp))
                EditText(
                    value = password,
                    onValueChange = { password = it },
                    textStyle = SNUTTTypography.body1.copy(fontSize = 16.sp),
                    hint = stringResource(R.string.sign_in_password_hint),
                )
                Spacer(modifier = Modifier.height(25.dp))
                EditText(
                    value = passwordConfirm,
                    onValueChange = { passwordConfirm = it },
                    textStyle = SNUTTTypography.body1.copy(fontSize = 16.sp),
                    hint = stringResource(R.string.sign_up_password_confirm_hint),
                )
            }
        }
    }

    val checkAndPostPasswordChange: (String, String, String) -> Unit = { currentPassword, newPassword, newPasswordConfirm ->
        if (newPassword != newPasswordConfirm) {
            context.toast(context.getString(R.string.settings_user_config_password_confirm_fail))
        } else {
            scope.launch {
                launchSuspendApi(apiOnProgress, apiOnError) {
                    viewModel.changePassword(
                        currentPassword,
                        newPassword,
                    )
                    context.toast(context.getString(R.string.settings_user_config_change_password_success))
                    passwordChangeDialogState = false
                }
            }
        }
    }

    if (passwordChangeDialogState) {
        var currentPassword by remember { mutableStateOf("") }
        var newPassword by remember { mutableStateOf("") }
        var newPasswordConfirm by remember { mutableStateOf("") }

        CustomDialog(
            onDismiss = { passwordChangeDialogState = false },
            onConfirm = { checkAndPostPasswordChange(currentPassword, newPassword, newPasswordConfirm) },
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
                    keyboardActions = KeyboardActions(onDone = { checkAndPostPasswordChange(currentPassword, newPassword, newPasswordConfirm) }),
                    visualTransformation = PasswordVisualTransformation(),
                    hint = stringResource(R.string.settings_user_config_new_password_confirm_hint),
                )
            }
        }
    }

    if (leaveDialogState) {
        CustomDialog(
            onDismiss = { leaveDialogState = false },
            onConfirm = {
                scope.launch {
                    launchSuspendApi(apiOnProgress, apiOnError) {
                        viewModel.leave()
                        leaveDialogState = false
                        navController.navigateAsOrigin(NavigationDestination.Tutorial)
                    }
                }
            },
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
    UserConfigPage()
}
