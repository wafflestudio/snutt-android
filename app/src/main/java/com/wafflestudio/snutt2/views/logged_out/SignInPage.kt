package com.wafflestudio.snutt2.views.logged_out

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.EditText
import com.wafflestudio.snutt2.components.compose.SimpleTopBar
import com.wafflestudio.snutt2.components.compose.WebViewStyleButton
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.views.*
import com.wafflestudio.snutt2.views.logged_in.home.HomeViewModel
import com.wafflestudio.snutt2.views.logged_in.home.settings.UserViewModel
import kotlinx.coroutines.launch

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
    val buttonEnabled by remember { derivedStateOf { idField.isNotEmpty() && passwordField.isNotEmpty() } }

    val handleLocalSignIn = {
        coroutineScope.launch {
            launchSuspendApi(
                apiOnProgress = apiOnProgress,
                apiOnError = apiOnError,
                loadingIndicatorTitle = context.getString(R.string.sign_in_sign_in_button),
            ) {
                userViewModel.loginLocal(idField, passwordField)
                homeViewModel.refreshData()
                navController.navigateAsOrigin(NavigationDestination.Home)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SNUTTColors.White900)
            .clicks { focusManager.clearFocus() },
    ) {
        SimpleTopBar(
            title = stringResource(R.string.sign_in_app_bar_title),
            onClickNavigateBack = { navController.popBackStack() },
        )

        Column(
            modifier = Modifier
                .padding(20.dp),
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier
                    .weight(1f),
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = stringResource(R.string.sign_in_id_title),
                        style = SNUTTTypography.subtitle2.copy(color = SNUTTColors.Black600),
                    )
                    EditText(
                        value = idField,
                        onValueChange = { idField = it },
                        hint = stringResource(R.string.sign_in_id_hint),
                        textStyle = SNUTTTypography.subtitle2.copy(color = SNUTTColors.Black900),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        singleLine = true,
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = stringResource(R.string.sign_in_password_title),
                        style = SNUTTTypography.subtitle2.copy(color = SNUTTColors.Black600),
                    )
                    EditText(
                        value = passwordField,
                        onValueChange = { passwordField = it },
                        hint = stringResource(R.string.sign_in_password_hint),
                        textStyle = SNUTTTypography.subtitle2.copy(color = SNUTTColors.Black900),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        keyboardActions = KeyboardActions(onDone = { handleLocalSignIn() }),
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.sign_in_find_id_button),
                        style = SNUTTTypography.subtitle2.copy(color = SNUTTColors.Black600),
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clicks { navController.navigate(NavigationDestination.FindId) },
                    )

                    Text(
                        text = "|",
                        style = SNUTTTypography.subtitle2.copy(color = SNUTTColors.Black600),
                        modifier = Modifier.padding(horizontal = 15.dp),
                    )

                    Text(
                        text = stringResource(R.string.sign_in_find_password_button),
                        style = SNUTTTypography.subtitle2.copy(color = SNUTTColors.Black600),
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clicks { navController.navigate(NavigationDestination.FindPassword) },
                    )
                }
            }

            WebViewStyleButton(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .height(45.dp)
                    .fillMaxWidth(),
                enabled = buttonEnabled,
                onClick = { handleLocalSignIn() },
            ) {
                Text(
                    text = stringResource(R.string.sign_in_sign_in_button),
                    style = SNUTTTypography.button.copy(
                        color = if (buttonEnabled) SNUTTColors.AllWhite else SNUTTColors.Gray600,
                    ),
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
