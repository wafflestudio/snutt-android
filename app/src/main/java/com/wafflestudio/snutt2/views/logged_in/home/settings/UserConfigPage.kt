package com.wafflestudio.snutt2.views.logged_in.home.settings

import androidx.activity.result.ActivityResultRegistryOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.*
import com.wafflestudio.snutt2.lib.android.toast
import com.wafflestudio.snutt2.views.NavControllerContext
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.Margin
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun UserConfigPage() {
    val context = LocalContext.current
    val navController = NavControllerContext.current
    val scope = rememberCoroutineScope()

    val viewModel = hiltViewModel<UserViewModel>()
    val user = viewModel.userInfo.collectAsState()

    var addIdPasswordDialogState by remember { mutableStateOf(false) }
    var passwordChangeDialogState by remember { mutableStateOf(false) }
    var emailChangeDialogState by remember { mutableStateOf(false) }
    var disconnectFacebookDialogState by remember { mutableStateOf(false) }
    var leaveDialogState by remember { mutableStateOf(false) }

    val hasLocalId by remember { mutableStateOf(user.value.localId.isNullOrEmpty().not()) }
    var facebookConnected by remember { mutableStateOf(user.value.fbName.isNullOrEmpty().not()) }

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
                    scope.launch {
                        viewModel.connectFacebook(id, token)
                        facebookConnected = true
                        // TODO: onSuccess, onError 분기
                        viewModel.fetchUserFacebook().name
                        // fetchUserInfo 와는 다른 성격? connectFacebook 이 성공하면
                        // userInfo.fbName 항목은 따로 업데이트되지 않는건가?
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
            .background(Color(0xfff2f2f2)) // TODO: Color
    ) {
        TopAppBar(
            title = { Text(text = stringResource(R.string.user_settings_app_bar_title)) },
            navigationIcon = {
                ArrowBackIcon(
                    modifier = Modifier
                        .size(30.dp)
                        .clicks {
                            navController.popBackStack()
                        }
                )
            }
        )
        Margin(height = 10.dp)
        Column(modifier = Modifier.background(Color.White)) {
            if (hasLocalId) {
                SettingItem(title = stringResource(R.string.sign_in_id_hint), content = {
                    Text(text = user.value.localId.toString())
                })
                SettingItem(
                    title = stringResource(R.string.settings_user_config_change_password),
                    content = {
                        ArrowRight(modifier = Modifier.size(16.dp))
                    }
                ) { passwordChangeDialogState = true }
            } else {
                SettingItem(
                    title = stringResource(R.string.settings_user_config_add_local_id),
                    modifier = Modifier.background(Color.White),
                    content = {
                        ArrowRight(modifier = Modifier.size(16.dp))
                    }
                ) { addIdPasswordDialogState = true }
            }
        }
        Margin(height = 10.dp)
        if (facebookConnected) {
            SettingItem(
                title = stringResource(R.string.settings_user_config_connect_facebook),
                modifier = Modifier.background(Color.White),
                content = {
                    ArrowRight(modifier = Modifier.size(16.dp))
                }
            ) {
                LoginManager.getInstance().logInWithReadPermissions(
                    context as ActivityResultRegistryOwner, callbackManager, emptyList()
                )
            }
        } else {
            SettingItem(
                title = stringResource(R.string.settings_user_config_facebook_name),
                content = {
                    Text(text = user.value.fbName.toString())
                }
            ) {}
            SettingItem(
                title = stringResource(R.string.settings_user_config_facebook_disconnect),
                content = {
                    ArrowRight(modifier = Modifier.size(16.dp))
                }
            ) { disconnectFacebookDialogState = true }
        }
        Margin(height = 10.dp)
        Column(modifier = Modifier.background(Color.White)) {
            SettingItem(title = stringResource(R.string.settings_app_report_email), content = {
                Text(text = user.value.email ?: "")
            })
            SettingItem(
                title = stringResource(R.string.settings_user_config_change_email),
                content = {
                    ArrowRight(modifier = Modifier.size(16.dp))
                }
            ) { emailChangeDialogState = true }
        }
        Margin(height = 10.dp)
        SettingItem(
            title = stringResource(R.string.settings_user_config_leave),
            modifier = Modifier.background(Color.White),
            content = {
                ArrowRight(modifier = Modifier.size(16.dp))
            }
        ) { leaveDialogState = true }
    }

    if (addIdPasswordDialogState) {
        var id by remember { mutableStateOf("아이디(힌트 미구현)") }
        var password by remember { mutableStateOf("비밀번호(힌트 미구현)") }
        var passwordConfirm by remember { mutableStateOf("비밀번호 확인(힌트 미구현)") }

        CustomDialog(
            onDismiss = { addIdPasswordDialogState = false },
            onConfirm = {
                if (password != passwordConfirm) {
                    context.toast(context.getString(R.string.settings_user_config_password_confirm_fail))
                } else {
                    scope.launch {
                        viewModel.addNewLocalId(id, password)
                        context.toast(context.getString(R.string.settings_user_config_add_local_id_success))
                    }
                }
            },
            title = stringResource(R.string.settings_user_config_add_local_id),
            positiveButtonText = stringResource(
                R.string.notifications_noti_add
            )
        ) {
            EditText(value = id, onValueChange = { id = it })
            Spacer(modifier = Modifier.height(15.dp))
            EditText(value = password, onValueChange = { password = it })
            Spacer(modifier = Modifier.height(15.dp))
            EditText(value = passwordConfirm, onValueChange = { passwordConfirm = it })
        }
    }

    if (emailChangeDialogState) {
        var email by remember { mutableStateOf("이메일(힌트 미구현)") }

        CustomDialog(
            onDismiss = { emailChangeDialogState = false },
            onConfirm = {
                if (email.isEmpty()) {
                    context.toast(context.getString(R.string.settings_user_config_enter_email))
                } else {
                    scope.launch {
                        viewModel.changeEmail(email)
                        context.toast(context.getString(R.string.settings_user_config_change_email_success))
                    }
                }
            },
            title = stringResource(R.string.settings_user_config_change_email),
            positiveButtonText = stringResource(
                R.string.notifications_noti_change
            )
        ) {
            EditText(value = email, onValueChange = { email = it })
        }
    }

    if (passwordChangeDialogState) {
        var currentPassword by remember { mutableStateOf("현재 비밀번호(힌트 미구현)") }
        var newPassword by remember { mutableStateOf("새로운 바밀번호(힌트 미구현)") }
        var newPasswordConfirm by remember { mutableStateOf("새로운 비밀번호 확인(힌트 미구현)") }

        CustomDialog(
            onDismiss = { passwordChangeDialogState = false },
            onConfirm = {
                if (newPassword != newPasswordConfirm) {
                    context.toast(context.getString(R.string.settings_user_config_password_confirm_fail))
                } else {
                    scope.launch {
                        viewModel.changePassword(
                            currentPassword, newPassword
                        )
                        context.toast(context.getString(R.string.settings_user_config_change_password_success))
                    }
                }
            },
            title = stringResource(R.string.settings_user_config_change_password),
            positiveButtonText = stringResource(
                R.string.notifications_noti_change
            )
        ) {
            EditText(value = currentPassword, onValueChange = { currentPassword = it })
            Spacer(modifier = Modifier.height(15.dp))
            EditText(value = newPassword, onValueChange = { newPassword = it })
            Spacer(modifier = Modifier.height(15.dp))
            EditText(value = newPasswordConfirm, onValueChange = { newPasswordConfirm = it })
        }
    }

    if (leaveDialogState) {
        CustomDialog(
            onDismiss = { leaveDialogState = false },
            onConfirm = {
                scope.launch {
                    viewModel.leave()
                }
            },
            title = stringResource(R.string.settings_user_config_leave),
            positiveButtonText = stringResource(R.string.settings_user_config_leave)
        ) {
            Text(text = stringResource(R.string.settings_leave_message))
        }
    }

    if (disconnectFacebookDialogState) {
        CustomDialog(
            onDismiss = { disconnectFacebookDialogState = false },
            onConfirm = {
                scope.launch {
                    viewModel.disconnectFacebook()
                    // TODO: onSuccess, onError 분기
                    LoginManager.getInstance().logOut()
                    facebookConnected = true
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
