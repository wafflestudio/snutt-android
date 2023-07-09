package com.wafflestudio.snutt2.views.logged_out

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.*
import com.wafflestudio.snutt2.lib.android.toast
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.views.*
import com.wafflestudio.snutt2.views.logged_in.home.HomeViewModel
import com.wafflestudio.snutt2.views.logged_in.home.settings.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun SignUpPage() {
    val navController = LocalNavController.current
    val context = LocalContext.current
    val apiOnError = LocalApiOnError.current
    val apiOnProgress = LocalApiOnProgress.current

    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()

    val userViewModel = hiltViewModel<UserViewModel>()
    val homeViewModel = hiltViewModel<HomeViewModel>()

    var idField by remember { mutableStateOf("") }
    var passwordField by remember { mutableStateOf("") }
    var passwordConfirmField by remember { mutableStateOf("") }
    var emailField by remember { mutableStateOf("") }
    val buttonEnabled by remember {
        derivedStateOf {
            idField.isNotEmpty() && passwordField.isNotEmpty() && passwordConfirmField.isNotEmpty() && emailField.isNotEmpty()
        }
    }

    val handleLocalSignUp = {
        val isPasswordConfirmPassed = (passwordConfirmField == passwordField)
        if (isPasswordConfirmPassed.not()) {
            context.toast(context.getString(R.string.sign_up_password_confirm_invalid_toast))
        } else {
            coroutineScope.launch {
                launchSuspendApi(
                    apiOnProgress = apiOnProgress,
                    apiOnError = apiOnError,
                    loadingIndicatorTitle = context.getString(R.string.sign_up_sign_up_button)
                ) {
                    userViewModel.signUpLocal(idField, emailField.plus(context.getString(R.string.sign_up_email_form)), passwordField)
                    homeViewModel.refreshData()
                    navController.navigate(NavigationDestination.EmailVerification)
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SNUTTColors.White900)
            .clicks { focusManager.clearFocus() }
    ) {
        SimpleTopBar(
            title = stringResource(R.string.sign_up_app_bar_title),
            onClickNavigateBack = { navController.popBackStack() }
        )

        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = stringResource(R.string.sign_up_id_title),
                        style = SNUTTTypography.subtitle2.copy(color = SNUTTColors.Black600),
                    )
                    EditText(
                        value = idField,
                        onValueChange = { idField = it },
                        hint = stringResource(R.string.sign_up_id_hint),
                        textStyle = SNUTTTypography.subtitle2.copy(color = SNUTTColors.Black900),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = {
                            focusManager.moveFocus(
                                FocusDirection.Down
                            )
                        }),
                        singleLine = true,
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = stringResource(R.string.sign_up_password_title),
                        style = SNUTTTypography.subtitle2.copy(color = SNUTTColors.Black600)
                    )
                    EditText(
                        value = passwordField,
                        onValueChange = { passwordField = it },
                        hint = stringResource(R.string.sign_up_password_hint),
                        textStyle = SNUTTTypography.subtitle2.copy(color = SNUTTColors.Black900),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(onNext = {
                            focusManager.moveFocus(
                                FocusDirection.Down
                            )
                        }),
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = stringResource(R.string.sign_up_password_confirm_title),
                        style = SNUTTTypography.subtitle2.copy(color = SNUTTColors.Black600),
                    )
                    EditText(
                        value = passwordConfirmField,
                        onValueChange = { passwordConfirmField = it },
                        hint = stringResource(R.string.sign_up_password_confirm_hint),
                        textStyle = SNUTTTypography.subtitle2.copy(color = SNUTTColors.Black900),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(onNext = {
                            focusManager.moveFocus(
                                FocusDirection.Down
                            )
                        }),
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = stringResource(R.string.sign_up_email_input_title),
                        style = SNUTTTypography.subtitle2.copy(color = SNUTTColors.Black600)
                    )
                    EditText(
                        value = emailField,
                        onValueChange = { emailField = it },
                        hint = stringResource(R.string.sign_up_email_input_hint),
                        textStyle = SNUTTTypography.subtitle2.copy(color = SNUTTColors.Black900),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { handleLocalSignUp() }),
                        singleLine = true,
                        trailingIcon = {
                            Text(
                                text = stringResource(R.string.sign_up_email_form),
                                style = SNUTTTypography.subtitle2.copy(color = SNUTTColors.Black900),
                                textAlign = TextAlign.Right,
                                maxLines = 1,
                            )
                        }
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(modifier = Modifier.padding(top = 20.dp)) {
                    Text(
                        text = stringResource(id = R.string.sign_up_terms_1) + " ",
                        style = SNUTTTypography.body2,
                    )
                    Text(
                        text = stringResource(id = R.string.sign_up_terms_2),
                        style = SNUTTTypography.body2.copy(fontWeight = FontWeight.Bold),
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clicks {
                            val termsPageUrl =
                                context.getString(R.string.api_server) + context.getString(R.string.terms)
                            val intent =
                                Intent(Intent.ACTION_VIEW, Uri.parse(termsPageUrl))
                            context.startActivity(intent)
                        }
                    )
                    Text(
                        text = stringResource(id = R.string.sign_up_terms_3),
                        style = SNUTTTypography.body2,
                    )
                }

                WebViewStyleButton(
                    color = if (buttonEnabled) SNUTTColors.SNUTTTheme else SNUTTColors.Gray400,
                    cornerRadius = 10.dp,
                    onClick = { handleLocalSignUp() },
                    modifier = Modifier
                        .height(45.dp)
                        .fillMaxWidth(),
                ) {
                    Text(
                        text = stringResource(R.string.sign_up_sign_up_button),
                        style = SNUTTTypography.button.copy(
                            color = if (buttonEnabled) SNUTTColors.AllWhite else SNUTTColors.Gray600
                        )
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun SignUpPagePreview() {
    SignUpPage()
}
