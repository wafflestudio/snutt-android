package com.wafflestudio.snutt2.views.logged_out

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
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.ProgressDialog
import com.wafflestudio.snutt2.views.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx3.await

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SignInPage() {
    val navController = LocalNavController.current
    val apiOnError = LocalApiOnError.current
    val apiOnProgress = LocalApiOnProgress.current
    val keyboardManager = LocalSoftwareKeyboardController.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val authViewModel = hiltViewModel<AuthViewModel>()

    var idField by remember { mutableStateOf("") }
    var passwordField by remember { mutableStateOf("") }

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
                    coroutineScope.launch {
                        try {
                            apiOnProgress.showProgress()
                            keyboardManager?.hide()
                            authViewModel.loginLocal(idField, passwordField).await()
                            navController.navigateAsOrigin(NavigationDestination.Home)
                        } catch (e: Exception) {
                            apiOnError(e)
                        } finally {
                            apiOnProgress.hideProgress()
                        }
                    }
                }
            ) {
                Text(text = stringResource(R.string.sign_in_sign_in_button))
            }

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp),
                onClick = {
                    coroutineScope.launch {
                        try {
                            apiOnProgress.showProgress()
                            val loginResult = facebookLogin(context)
                            authViewModel.loginFacebook(
                                loginResult.accessToken.userId,
                                loginResult.accessToken.token
                            ).await()
                            navController.navigateAsOrigin(NavigationDestination.Home)
                        } catch (e: Exception) {
                            apiOnError(e)
                        } finally {
                            apiOnProgress.hideProgress()
                        }
                    }
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
}

@Preview(showBackground = true)
@Composable
fun SignInPagePreview() {
    SignInPage()
}
