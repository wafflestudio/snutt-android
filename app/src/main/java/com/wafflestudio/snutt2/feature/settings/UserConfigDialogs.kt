package com.wafflestudio.snutt2.feature.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.ui.components.compose.CustomDialog
import com.wafflestudio.snutt2.ui.components.compose.EditText
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewSurface
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography

@Composable
fun UserConfigDialogs(
    dialogState: UserConfigUiState.DialogState,
    onChangePasswordCurrentChange: (String) -> Unit,
    onChangePasswordNewChange: (String) -> Unit,
    onChangePasswordNewConfirmChange: (String) -> Unit,
    onConfirmChangePassword: () -> Unit,
    onDismissChangePassword: () -> Unit,
    onAddIdPasswordIdChange: (String) -> Unit,
    onAddIdPasswordPasswordChange: (String) -> Unit,
    onAddIdPasswordPasswordConfirmChange: (String) -> Unit,
    onConfirmAddIdPassword: () -> Unit,
    onDismissAddIdPassword: () -> Unit,
    onConfirmLeave: () -> Unit,
    onDismissLeave: () -> Unit,
) {
    when (dialogState) {
        UserConfigUiState.DialogState.None -> {}

        is UserConfigUiState.DialogState.ChangePassword -> {
            CustomDialog(
                onDismiss = onDismissChangePassword,
                onConfirm = onConfirmChangePassword,
                title = stringResource(R.string.settings_user_config_change_password),
                positiveButtonText = stringResource(R.string.notifications_noti_change),
            ) {
                val focusManager = LocalFocusManager.current
                Column {
                    EditText(
                        value = dialogState.currentPassword,
                        onValueChange = onChangePasswordCurrentChange,
                        textStyle = SNUTTTypography.body1.copy(fontSize = 16.sp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        visualTransformation = PasswordVisualTransformation(),
                        hint = stringResource(R.string.settings_user_config_current_password_hint),
                    )
                    Spacer(modifier = Modifier.height(25.dp))
                    EditText(
                        value = dialogState.newPassword,
                        onValueChange = onChangePasswordNewChange,
                        textStyle = SNUTTTypography.body1.copy(fontSize = 16.sp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        visualTransformation = PasswordVisualTransformation(),
                        hint = stringResource(R.string.settings_user_config_new_password_hint),
                    )
                    Spacer(modifier = Modifier.height(25.dp))
                    EditText(
                        value = dialogState.newPasswordConfirm,
                        onValueChange = onChangePasswordNewConfirmChange,
                        textStyle = SNUTTTypography.body1.copy(fontSize = 16.sp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { onConfirmChangePassword() }),
                        visualTransformation = PasswordVisualTransformation(),
                        hint = stringResource(R.string.settings_user_config_new_password_confirm_hint),
                    )
                }
            }
        }

        is UserConfigUiState.DialogState.AddIdPassword -> {
            CustomDialog(
                onDismiss = onDismissAddIdPassword,
                onConfirm = onConfirmAddIdPassword,
                title = stringResource(R.string.settings_user_config_add_local_id),
                positiveButtonText = stringResource(R.string.notifications_noti_add),
            ) {
                val focusManager = LocalFocusManager.current
                Column {
                    EditText(
                        value = dialogState.id,
                        onValueChange = onAddIdPasswordIdChange,
                        textStyle = SNUTTTypography.body1.copy(fontSize = 16.sp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        hint = stringResource(R.string.sign_in_id_hint),
                    )
                    Spacer(modifier = Modifier.height(25.dp))
                    EditText(
                        value = dialogState.password,
                        onValueChange = onAddIdPasswordPasswordChange,
                        textStyle = SNUTTTypography.body1.copy(fontSize = 16.sp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        visualTransformation = PasswordVisualTransformation(),
                        hint = stringResource(R.string.sign_in_password_hint),
                    )
                    Spacer(modifier = Modifier.height(25.dp))
                    EditText(
                        value = dialogState.passwordConfirm,
                        onValueChange = onAddIdPasswordPasswordConfirmChange,
                        textStyle = SNUTTTypography.body1.copy(fontSize = 16.sp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { onConfirmAddIdPassword() }),
                        visualTransformation = PasswordVisualTransformation(),
                        hint = stringResource(R.string.sign_up_password_confirm_hint),
                    )
                }
            }
        }

        UserConfigUiState.DialogState.Leave -> {
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
}

@SnuttPreview
@Composable
private fun UserConfigDialogs_ChangePassword() {
    SnuttPreviewSurface {
        UserConfigDialogs(
            dialogState = UserConfigUiState.DialogState.ChangePassword(),
            onChangePasswordCurrentChange = {},
            onChangePasswordNewChange = {},
            onChangePasswordNewConfirmChange = {},
            onConfirmChangePassword = {},
            onDismissChangePassword = {},
            onAddIdPasswordIdChange = {},
            onAddIdPasswordPasswordChange = {},
            onAddIdPasswordPasswordConfirmChange = {},
            onConfirmAddIdPassword = {},
            onDismissAddIdPassword = {},
            onConfirmLeave = {},
            onDismissLeave = {},
        )
    }
}

@SnuttPreview
@Composable
private fun UserConfigDialogs_AddIdPassword() {
    SnuttPreviewSurface {
        UserConfigDialogs(
            dialogState = UserConfigUiState.DialogState.AddIdPassword(),
            onChangePasswordCurrentChange = {},
            onChangePasswordNewChange = {},
            onChangePasswordNewConfirmChange = {},
            onConfirmChangePassword = {},
            onDismissChangePassword = {},
            onAddIdPasswordIdChange = {},
            onAddIdPasswordPasswordChange = {},
            onAddIdPasswordPasswordConfirmChange = {},
            onConfirmAddIdPassword = {},
            onDismissAddIdPassword = {},
            onConfirmLeave = {},
            onDismissLeave = {},
        )
    }
}

@SnuttPreview
@Composable
private fun UserConfigDialogs_Leave() {
    SnuttPreviewSurface {
        UserConfigDialogs(
            dialogState = UserConfigUiState.DialogState.Leave,
            onChangePasswordCurrentChange = {},
            onChangePasswordNewChange = {},
            onChangePasswordNewConfirmChange = {},
            onConfirmChangePassword = {},
            onDismissChangePassword = {},
            onAddIdPasswordIdChange = {},
            onAddIdPasswordPasswordChange = {},
            onAddIdPasswordPasswordConfirmChange = {},
            onConfirmAddIdPassword = {},
            onDismissAddIdPassword = {},
            onConfirmLeave = {},
            onDismissLeave = {},
        )
    }
}
