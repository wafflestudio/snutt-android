package com.wafflestudio.snutt2.views.logged_out

import android.widget.Toast
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
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
import com.wafflestudio.snutt2.components.compose.ProgressDialog
import com.wafflestudio.snutt2.views.NavControllerContext
import com.wafflestudio.snutt2.views.NavigationDestination.home
import com.wafflestudio.snutt2.views.navigateAsOrigin
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.kotlin.subscribeBy

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SignInPage() {
    val navController = NavControllerContext.current
    val authViewModel = hiltViewModel<AuthViewModel>()

    var idField by remember { mutableStateOf("") }
    var passwordField by remember { mutableStateOf("") }

    val keyboardManager = LocalSoftwareKeyboardController.current
    val callbackManager = CallbackManager.Factory.create()
    val loginManager = LoginManager.getInstance()
    val context = LocalContext.current

    var showProgressDialog by remember { mutableStateOf(false) }
    if (showProgressDialog) {
        ProgressDialog(
            title = stringResource(R.string.sign_in_sign_in_button),
            message = stringResource(R.string.sign_in_progress_bar_message),
            onDismissRequest = { showProgressDialog = false }
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.sign_in_logo_title),
                modifier = Modifier.padding(top = 40.dp, bottom = 15.dp),
            )

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = stringResource(R.string.sign_in_logo_title)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier
                .weight(1f)
                .padding(top = 60.dp, bottom = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            TextField(
                value = idField,
                onValueChange = { idField = it },
                placeholder = {
                    Text(text = stringResource(R.string.sign_in_id_hint))
                },
                modifier = Modifier.fillMaxWidth(),
            )

            TextField(
                value = passwordField,
                onValueChange = { passwordField = it },
                placeholder = {
                    Text(text = stringResource(R.string.sign_in_password_hint))
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.wrapContentHeight()
        ) {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp),
                shape = RectangleShape,
                onClick = {
                    keyboardManager?.hide()
                    showProgressDialog = true
                    authViewModel.loginLocal(idField, passwordField)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeBy( // TODO: dispose
                            onSuccess = {
                                navController.navigateAsOrigin(home)
                                showProgressDialog = false
                            },
                            onError = { // TODO: onError
                                showProgressDialog = false
                            }
                        )
                }
            ) {
                Text(text = stringResource(R.string.sign_in_sign_in_button))
            }

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp),
                onClick = {
                    loginManager.logInWithReadPermissions(
                        context as ActivityResultRegistryOwner,
                        callbackManager,
                        emptyList()
                    )
                }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.iconfacebook),
                    contentDescription = stringResource(id = R.string.sign_in_sign_in_facebook_button),
                    modifier = Modifier.padding(end = 12.dp)
                )

                Text(text = stringResource(R.string.sign_in_sign_in_facebook_button))
            }
        }
    }

    loginManager.registerCallback(
        callbackManager,
        object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                val id = result.accessToken.userId
                val token = result.accessToken.token
                authViewModel.loginFacebook(id, token) // TODO: dispose
                    .subscribeBy(
                        onError = {}, // TODO: onError
                        onSuccess = { navController.navigateAsOrigin(home) }
                    )
            }

            override fun onCancel() {
                Toast.makeText(
                    context,
                    context.getString(R.string.sign_up_facebook_login_failed_toast),
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onError(error: FacebookException) {
                Toast.makeText(
                    context,
                    context.getString(R.string.sign_up_facebook_login_failed_toast),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun SignInPagePreview() {
    SignInPage()
}
