package com.wafflestudio.snutt2.views.logged_in.home.settings

import androidx.activity.result.ActivityResultRegistryOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.*
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.ArrowRight
import com.wafflestudio.snutt2.components.compose.CustomDialog
import com.wafflestudio.snutt2.components.compose.EditText
import com.wafflestudio.snutt2.components.compose.SimpleTopBar
import com.wafflestudio.snutt2.lib.android.toast
import com.wafflestudio.snutt2.lib.network.dto.core.UserDto
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.views.*
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.Margin
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun UserConfigPage() {
    val context = LocalContext.current
    val navController = LocalNavController.current
    val scope = rememberCoroutineScope()
    val apiOnProgress = LocalApiOnProgress.current
    val apiOnError = LocalApiOnError.current

    val viewModel = hiltViewModel<UserViewModel>()
    val user: UserDto? by viewModel.userInfo.collectAsState()

    var addIdPasswordDialogState by remember { mutableStateOf(false) }
    var passwordChangeDialogState by remember { mutableStateOf(false) }
    var disconnectFacebookDialogState by remember { mutableStateOf(false) }
    var leaveDialogState by remember { mutableStateOf(false) }

    var facebookConnected by remember(user?.fbName) {
        mutableStateOf(
            user?.fbName.isNullOrEmpty().not()
        )
    }

    val callbackManager = CallbackManager.Factory.create()
    LoginManager.getInstance()
        .registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    // App code
                    val id = result.accessToken.userId
                    val token = result.accessToken.token
                    Timber.i("User ID: %s", result.accessToken.userId)
                    Timber.i("Auth Token: %s", result.accessToken.token)
                    // FIXME: staging 에서 테스트 불가
                    scope.launch {
                        launchSuspendApi(apiOnProgress, apiOnError) {
                            viewModel.connectFacebook(id, token)
                            facebookConnected = true
                            viewModel.fetchUserFacebook().name
                            // fetchUserInfo 와는 다른 성격? connectFacebook 이 성공하면
                            // userInfo.fbName 항목은 따로 업데이트되지 않는건가?
                        }
                    }
                }

                override fun onCancel() {
                    // App code
                    Timber.w("Cancel")
                    context.toast(context.getString(R.string.sign_up_facebook_login_failed_toast))
                }

                override fun onError(error: FacebookException) {
                    // App code
                    Timber.e(error)
                    context.toast(context.getString(R.string.sign_up_facebook_login_failed_toast))
                }
            }
        )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SNUTTColors.Gray100)
    ) {
        SimpleTopBar(
            title = stringResource(R.string.user_settings_app_bar_title),
            onClickNavigateBack = { navController.popBackStack() }
        )
        Margin(height = 10.dp)
        Column(modifier = Modifier.background(SNUTTColors.White900)) {
            if (user?.localId.isNullOrEmpty().not()) {
                SettingItem(title = stringResource(R.string.sign_in_id_hint), content = {
                    Text(text = user?.localId.toString(), style = SNUTTTypography.body2)
                })
                SettingItem(
                    title = stringResource(R.string.settings_user_config_change_password),
                    content = {
                        ArrowRight(
                            modifier = Modifier.size(16.dp),
                            colorFilter = ColorFilter.tint(SNUTTColors.Black900)
                        )
                    }
                ) { passwordChangeDialogState = true }
            } else {
                SettingItem(
                    title = stringResource(R.string.settings_user_config_add_local_id),
                    modifier = Modifier.background(SNUTTColors.White900),
                    content = {
                        ArrowRight(
                            modifier = Modifier.size(16.dp),
                            colorFilter = ColorFilter.tint(SNUTTColors.Black900)
                        )
                    }
                ) { addIdPasswordDialogState = true }
            }
        }
        Margin(height = 10.dp)
        if (facebookConnected) {
            SettingItem(
                title = stringResource(R.string.settings_user_config_facebook_name),
                modifier = Modifier.background(SNUTTColors.White900),
                content = {
                    Text(text = user?.fbName ?: "")
                }
            ) {}
            SettingItem(
                title = stringResource(R.string.settings_user_config_facebook_disconnect),
                modifier = Modifier.background(SNUTTColors.White900),
                content = {
                    ArrowRight(
                        modifier = Modifier.size(16.dp),
                        colorFilter = ColorFilter.tint(SNUTTColors.Black900)
                    )
                }
            ) { disconnectFacebookDialogState = true }
        } else {
            SettingItem(
                title = stringResource(R.string.settings_user_config_connect_facebook),
                modifier = Modifier.background(SNUTTColors.White900),
                content = {
                    ArrowRight(
                        modifier = Modifier.size(16.dp),
                        colorFilter = ColorFilter.tint(SNUTTColors.Black900)
                    )
                }
            ) {
                // FIXME: 실패했을 때.
                LoginManager.getInstance().logInWithReadPermissions(
                    context as ActivityResultRegistryOwner, callbackManager, emptyList()
                )
            }
        }
        Margin(height = 10.dp)
        Column(modifier = Modifier.background(SNUTTColors.White900)) {
            SettingItem(title = stringResource(R.string.settings_app_report_email), content = {
                Text(text = user?.email ?: "", style = SNUTTTypography.body2)
            })
        }
        Margin(height = 10.dp)
        SettingItem(
            title = stringResource(R.string.settings_user_config_leave),
            modifier = Modifier.background(SNUTTColors.White900),
            content = {
                ArrowRight(
                    modifier = Modifier.size(16.dp),
                    colorFilter = ColorFilter.tint(SNUTTColors.Black900)
                )
            }
        ) { leaveDialogState = true }
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
                        }
                    }
                }
            },
            title = stringResource(R.string.settings_user_config_add_local_id),
            positiveButtonText = stringResource(
                R.string.notifications_noti_add
            )
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
                        currentPassword, newPassword
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
                R.string.notifications_noti_change
            )
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
                    hint = stringResource(R.string.settings_user_config_current_password_hint)
                )
                Spacer(modifier = Modifier.height(25.dp))
                EditText(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    textStyle = SNUTTTypography.body1.copy(fontSize = 16.sp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                    visualTransformation = PasswordVisualTransformation(),
                    hint = stringResource(R.string.settings_user_config_new_password_hint)
                )
                Spacer(modifier = Modifier.height(25.dp))
                EditText(
                    value = newPasswordConfirm,
                    onValueChange = { newPasswordConfirm = it },
                    textStyle = SNUTTTypography.body1.copy(fontSize = 16.sp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { checkAndPostPasswordChange(currentPassword, newPassword, newPasswordConfirm) }),
                    visualTransformation = PasswordVisualTransformation(),
                    hint = stringResource(R.string.settings_user_config_new_password_confirm_hint)
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
            positiveButtonText = stringResource(R.string.settings_user_config_leave)
        ) {
            Text(text = stringResource(R.string.settings_leave_message), style = SNUTTTypography.body2)
        }
    }

    if (disconnectFacebookDialogState) {
        CustomDialog(
            onDismiss = { disconnectFacebookDialogState = false },
            onConfirm = {
                scope.launch {
                    launchSuspendApi(apiOnProgress, apiOnError) {
                        viewModel.disconnectFacebook()
                        LoginManager.getInstance().logOut()
                        facebookConnected = false
                        disconnectFacebookDialogState = false
                    }
                }
            },
            title = stringResource(R.string.settings_user_config_facebook_disconnect),
            positiveButtonText = stringResource(
                R.string.notifications_noti_disconnect
            )
        ) {
            Text(text = stringResource(R.string.settings_user_config_disconnect_facebook_message))
        }
    }
}

@Preview
@Composable
fun UserConfigPagePreview() {
    UserConfigPage()
}
