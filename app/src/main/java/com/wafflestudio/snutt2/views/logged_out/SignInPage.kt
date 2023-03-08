package com.wafflestudio.snutt2.views.logged_out

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.BorderButton
import com.wafflestudio.snutt2.components.compose.EditText
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.lib.facebookLogin
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.views.*
import com.wafflestudio.snutt2.views.logged_in.home.HomeViewModel
import com.wafflestudio.snutt2.views.logged_in.home.settings.UserViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SignInPage() {
    val navController = LocalNavController.current
    val apiOnError = LocalApiOnError.current
    val apiOnProgress = LocalApiOnProgress.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    val userViewModel = hiltViewModel<UserViewModel>()
    val homeViewModel = hiltViewModel<HomeViewModel>()

    var idField by remember { mutableStateOf("") }
    var passwordField by remember { mutableStateOf("") }

    val handleLocalSignIn = {
        coroutineScope.launch {
            try {
                apiOnProgress.showProgress(context.getString(R.string.sign_in_sign_in_button))
                userViewModel.loginLocal(idField, passwordField)
                homeViewModel.refreshData()
                navController.navigateAsOrigin(NavigationDestination.Home)
            } catch (e: Exception) {
                apiOnError(e)
            } finally {
                apiOnProgress.hideProgress()
            }
        }
    }
    val handleFacebookSignIn = {
        coroutineScope.launch {
            try {
                apiOnProgress.showProgress(context.getString(R.string.sign_in_sign_in_button))
                val loginResult = facebookLogin(context)
                userViewModel.loginFacebook(
                    loginResult.accessToken.userId,
                    loginResult.accessToken.token
                )
                homeViewModel.refreshData()
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
            .background(SNUTTColors.White900)
            .padding(30.dp)
            .clicks { focusManager.clearFocus() }
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = stringResource(R.string.sign_in_logo_title),
                modifier = Modifier.padding(top = 20.dp, bottom = 15.dp),
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
                .padding(top = 50.dp, bottom = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            EditText(
                value = idField,
                onValueChange = { idField = it },
                hint = stringResource(R.string.sign_in_id_hint),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            EditText(
                value = passwordField,
                onValueChange = { passwordField = it },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                keyboardActions = KeyboardActions(onDone = { handleLocalSignIn() }),
                hint = stringResource(R.string.sign_in_password_hint),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.wrapContentHeight()
        ) {
            BorderButton(
                modifier = Modifier.fillMaxWidth(),
                color = SNUTTColors.Gray200,
                onClick = { handleLocalSignIn() }
            ) {
                Text(
                    text = stringResource(R.string.sign_in_sign_in_button),
                    style = SNUTTTypography.button
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .background(SNUTTColors.Gray100)
                        .height(1.dp)
                        .weight(1f)
                )

                Text(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    text = "or", style = SNUTTTypography.body2, color = SNUTTColors.Gray200
                )

                Box(
                    modifier = Modifier
                        .background(SNUTTColors.Gray100)
                        .height(1.dp)
                        .weight(1f)
                )
            }

            BorderButton(
                modifier = Modifier.fillMaxWidth(),
                color = SNUTTColors.FacebookBlue,
                onClick = { handleFacebookSignIn() }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.iconfacebook),
                        contentDescription = stringResource(id = R.string.sign_in_sign_in_facebook_button),
                        modifier = Modifier
                            .height(18.dp)
                            .padding(end = 12.dp),
                    )

                    Text(
                        text = stringResource(R.string.sign_in_sign_in_facebook_button),
                        color = SNUTTColors.FacebookBlue,
                        style = SNUTTTypography.button
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .background(SNUTTColors.Gray100)
                        .height(1.dp)
                        .weight(1f)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.sign_in_find_id_button),
                    style = SNUTTTypography.subtitle2,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clicks { navController.navigate(NavigationDestination.FindId) }
                )

                Text(
                    text = "|",
                    style = SNUTTTypography.subtitle2,
                    modifier = Modifier.padding(horizontal = 15.dp)
                )

                Text(
                    text = stringResource(R.string.sign_in_find_password_button),
                    style = SNUTTTypography.subtitle2,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clicks { navController.navigate(NavigationDestination.FindPassword) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignInPagePreview() {
    SignInPage()
}
