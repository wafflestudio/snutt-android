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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.R

@Composable
fun SignUpPage(
    onClickSignUp: (String, String, String, String) -> Unit,
    onClickFacebookSignUp: () -> Unit,
) {
    var idField by remember { mutableStateOf("") }
    var passwordField by remember { mutableStateOf("") }
    var passwordConfirmField by remember { mutableStateOf("") }
    var emailField by remember { mutableStateOf("") }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.sign_up_logo_title),
                modifier = Modifier.padding(top = 40.dp, bottom = 15.dp),
            )

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = stringResource(R.string.sign_up_logo_title)
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
                    Text(text = stringResource(R.string.sign_up_id_hint))
                },
                modifier = Modifier.fillMaxWidth(),
            )

            TextField(
                value = passwordField,
                onValueChange = { passwordField = it },
                placeholder = {
                    Text(text = stringResource(R.string.sign_up_password_hint))
                },
                modifier = Modifier.fillMaxWidth(),
            )

            TextField(
                value = emailField,
                onValueChange = { emailField = it },
                placeholder = {
                    Text(text = stringResource(R.string.sign_up_email_input_hint))
                },
                modifier = Modifier.fillMaxWidth(),
            )

            TextField(
                value = passwordConfirmField,
                onValueChange = { passwordConfirmField = it },
                placeholder = {
                    Text(text = stringResource(R.string.sign_up_password_confirm_hint))
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
                    onClickSignUp(
                        idField,
                        passwordField,
                        passwordConfirmField,
                        emailField
                    )
                }
            ) {
                Text(text = stringResource(R.string.sign_up_sign_up_button))
            }

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp),
                onClick = { onClickFacebookSignUp() }) {
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
}

@Preview
@Composable
fun SignUpPagePreview() {
    SignUpPage(
        onClickSignUp = { _, _, _, _ -> },
        onClickFacebookSignUp = {},
    )
}
