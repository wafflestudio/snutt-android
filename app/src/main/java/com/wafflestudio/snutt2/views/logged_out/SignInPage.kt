package com.wafflestudio.snutt2.views.logged_out

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.EditText
import com.wafflestudio.snutt2.components.compose.SimpleTopBar
import com.wafflestudio.snutt2.components.compose.WebViewStyleButton
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.lib.android.toast
import com.wafflestudio.snutt2.lib.logging.AnalyticsScreen
import com.wafflestudio.snutt2.lib.logging.logImpression
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography

@Composable
fun SignInPage(
    viewModel: SignInViewModel = hiltViewModel<SignInViewModel>(),
    onNavigateHome: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateFindId: () -> Unit,
    onNavigateFindPassword: () -> Unit,
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is SignInUiEvent.ShowToast -> context.toast(event.message)
                is SignInUiEvent.NavigateHome -> onNavigateHome()
            }
        }
    }

    SignInScreen(
        isLoading = uiState.isLoading,
        onSignIn = viewModel::signIn,
        onNavigateBack = onNavigateBack,
        onNavigateFindId = onNavigateFindId,
        onNavigateFindPassword = onNavigateFindPassword,
    )
}

@Composable
private fun SignInScreen(
    isLoading: Boolean,
    onSignIn: (id: String, password: String) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateFindId: () -> Unit,
    onNavigateFindPassword: () -> Unit,
) {
    val focusManager = LocalFocusManager.current

    // TODO: 상태 뷰모델로 올리기
    var idField by remember { mutableStateOf("") }
    var passwordField by remember { mutableStateOf("") }
    val buttonEnabled by remember { derivedStateOf { idField.isNotEmpty() && passwordField.isNotEmpty() } }

    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SNUTTColors.White900)
                .clicks { focusManager.clearFocus() }
                .logImpression(AnalyticsScreen.Login),
        ) {
            SimpleTopBar(
                title = stringResource(R.string.sign_in_app_bar_title),
                onClickNavigateBack = onNavigateBack,
            )

            Column(
                modifier = Modifier.padding(20.dp),
            ) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    modifier = Modifier.weight(1f),
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
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

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
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
                            keyboardActions = KeyboardActions(onDone = { onSignIn(idField, passwordField) }),
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true,
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(R.string.sign_in_find_id_button),
                            style = SNUTTTypography.subtitle2.copy(color = SNUTTColors.Black600),
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier.clicks { onNavigateFindId() },
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
                            modifier = Modifier.clicks { onNavigateFindPassword() },
                        )
                    }
                }

                WebViewStyleButton(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .height(45.dp)
                        .fillMaxWidth(),
                    enabled = buttonEnabled,
                    onClick = { onSignIn(idField, passwordField) },
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

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SNUTTColors.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = SNUTTColors.AllWhite)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SignInScreenPreview() {
    SignInScreen(
        isLoading = false,
        onSignIn = { _, _ -> },
        onNavigateBack = {},
        onNavigateFindId = {},
        onNavigateFindPassword = {},
    )
}
