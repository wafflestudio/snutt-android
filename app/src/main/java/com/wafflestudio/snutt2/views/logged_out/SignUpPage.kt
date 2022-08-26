package com.wafflestudio.snutt2.views.logged_out

import android.widget.Toast
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.EditText
import com.wafflestudio.snutt2.components.compose.ProgressDialog
import com.wafflestudio.snutt2.lib.android.toast
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.views.*
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.kotlin.subscribeBy
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx3.await

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SignUpPage() {
    val navController = LocalNavController.current
    val context = LocalContext.current
    val apiOnError = LocalApiOnError.current
    val apiOnProgress = LocalApiOnProgress.current

    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()

    val authViewModel = hiltViewModel<AuthViewModel>()

    var idField by remember { mutableStateOf("") }
    var passwordField by remember { mutableStateOf("") }
    var passwordConfirmField by remember { mutableStateOf("") }
    var emailField by remember { mutableStateOf("") }

    val handleLocalSignUp = {
        val isPasswordConfirmPassed = (passwordConfirmField == passwordField)
        if (isPasswordConfirmPassed.not()) {
            context.toast(context.getString(R.string.sign_up_password_confirm_invalid_toast))
        } else {
            coroutineScope.launch {
                try {
                    apiOnProgress.showProgress()
                    authViewModel.signUpLocal(idField, emailField, passwordField).await()
                    navController.navigateAsOrigin(NavigationDestination.Home)
                } catch (e: Exception) {
                    apiOnError(e)
                } finally {
                    apiOnProgress.hideProgress()
                }
            }
        }
    }
    val handleFacebookSignUp = {
        coroutineScope.launch {
            try {
                apiOnProgress.showProgress()
                val loginResult = facebookLogin(context)
                val id = loginResult.accessToken.userId
                val token = loginResult.accessToken.token
                authViewModel.signUpFacebook(id, token).await()
                navController.navigateAsOrigin(NavigationDestination.Home)
            } catch (e: Exception) {
                apiOnError(e)
            } finally {
                apiOnProgress.hideProgress()
            }
        }
    }


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = stringResource(R.string.sign_in_logo_title),
                modifier = Modifier.padding(top = 40.dp, bottom = 15.dp),
            )

            Text(
                text = stringResource(R.string.sign_in_logo_title),
                style = SNUTTTypography.h1,
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
            EditText(
                value = idField,
                onValueChange = { idField = it },
                hint = stringResource(R.string.sign_up_id_hint),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth(),
            )

            EditText(
                value = emailField,
                onValueChange = { emailField = it },
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                hint = stringResource(R.string.sign_up_email_input_hint),
                modifier = Modifier.fillMaxWidth(),
            )

            EditText(
                value = passwordField,
                onValueChange = { passwordField = it },
                hint = stringResource(R.string.sign_up_password_hint),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Password
                ),
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
            )

            EditText(
                value = passwordConfirmField,
                onValueChange = { passwordConfirmField = it },
                keyboardActions = KeyboardActions(onDone = { handleLocalSignUp() }),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Password
                ),
                visualTransformation = PasswordVisualTransformation(),
                hint = stringResource(R.string.sign_up_password_confirm_hint),
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
                onClick = { handleLocalSignUp() }
            ) {
                Text(text = stringResource(R.string.sign_up_sign_up_button))
            }

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp),
                onClick = { handleFacebookSignUp() }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.iconfacebook),
                    contentDescription = stringResource(id = R.string.sign_up_sign_up_facebook_button),
                    modifier = Modifier.padding(end = 12.dp)
                )
                Text(text = stringResource(R.string.sign_up_sign_up_facebook_button))
            }
        }
        Text(
            text = stringResource(id = R.string.sign_up_terms),
            modifier = Modifier.padding(top = 20.dp)
        )
    }

    // TODO: 이용 약관 버튼
}

@Preview
@Composable
fun SignUpPagePreview() {
    SignUpPage()
}
