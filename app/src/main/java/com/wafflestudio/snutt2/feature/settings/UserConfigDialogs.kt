package com.wafflestudio.snutt2.feature.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.ui.components.compose.CustomDialog
import com.wafflestudio.snutt2.ui.components.compose.EditText
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography

@Composable
fun UserConfigDialogs(
    dialogState: UserConfigUiState.DialogState,
    onConfirmChangePassword: (String, String, String) -> Unit,
    onDismissChangePassword: () -> Unit,
    onConfirmAddIdPassword: (String, String, String) -> Unit,
    onDismissAddIdPassword: () -> Unit,
    onConfirmLeave: () -> Unit,
    onDismissLeave: () -> Unit,
) {
    when (dialogState) {
        UserConfigUiState.DialogState.None -> {}

        UserConfigUiState.DialogState.ChangePassword -> {
            var currentPassword by remember { mutableStateOf("") }
            var newPassword by remember { mutableStateOf("") }
            var newPasswordConfirm by remember { mutableStateOf("") }

            CustomDialog(
                onDismiss = onDismissChangePassword,
                onConfirm = { onConfirmChangePassword(currentPassword, newPassword, newPasswordConfirm) },
                title = stringResource(R.string.settings_user_config_change_password),
                positiveButtonText = stringResource(R.string.notifications_noti_change),
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

        UserConfigUiState.DialogState.AddIdPassword -> {
            var id by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }
            var passwordConfirm by remember { mutableStateOf("") }

            CustomDialog(
                onDismiss = onDismissAddIdPassword,
                onConfirm = { onConfirmAddIdPassword(id, password, passwordConfirm) },
                title = stringResource(R.string.settings_user_config_add_local_id),
                positiveButtonText = stringResource(R.string.notifications_noti_add),
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
