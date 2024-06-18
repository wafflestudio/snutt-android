package com.wafflestudio.snutt2.views.logged_out

import android.app.Activity
import android.util.Log
import androidx.credentials.GetCredentialRequest
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.BorderButton
import com.wafflestudio.snutt2.lib.facebookLogin
import com.wafflestudio.snutt2.lib.handleSignIn
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.views.*
import com.wafflestudio.snutt2.views.logged_in.home.HomeViewModel
import com.wafflestudio.snutt2.views.logged_in.home.settings.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun TutorialPage() {
    val navController = LocalNavController.current
    val coroutineScope = rememberCoroutineScope()
    val apiOnError = LocalApiOnError.current
    val apiOnProgress = LocalApiOnProgress.current
    val context = LocalContext.current
    val activityContext = LocalContext.current as Activity

    val userViewModel = hiltViewModel<UserViewModel>()
    val homeViewModel = hiltViewModel<HomeViewModel>()

    val handleFacebookSignIn = {
        coroutineScope.launch {
            launchSuspendApi(
                apiOnProgress = apiOnProgress,
                apiOnError = apiOnError,
                loadingIndicatorTitle = context.getString(R.string.sign_in_sign_in_button),
            ) {
                val loginResult = facebookLogin(context)
                userViewModel.loginFacebook(
                    loginResult.accessToken.userId,
                    loginResult.accessToken.token,
                )
                homeViewModel.refreshData()
                navController.navigateAsOrigin(NavigationDestination.Home)
            }
        }
    }

    val credentialManager = CredentialManager.create(context)

    val getCredentialRequest2 = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(stringResource(R.string.web_client_id))
        .setAutoSelectEnabled(true)
        // .setNonce(<nonce string to use when generating a Google ID token>) // TODO: 이거 나중에 추가 해야함
        .build().let {
            GetCredentialRequest.Builder()
                .addCredentialOption(it)
                .build()
        }

    val getCredentialRequest = GetSignInWithGoogleOption.Builder(
        stringResource(R.string.web_client_id),
    )
        .build().let {
            GetCredentialRequest(listOf(it))
        }

//    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//        .requestEmail()
//        .requestIdToken(stringResource(R.string.web_client_id)) // Request ID token
//        .requestServerAuthCode(stringResource(R.string.web_client_id)) // Request server auth code
//        .requestScopes(Scope("https://www.googleapis.com/auth/userinfo.profile")) // Request profile scope
//        .build()

    val handleGoogleSignIn = {
        coroutineScope.launch {
            launchSuspendApi(
                apiOnProgress = apiOnProgress,
                apiOnError = apiOnError,
                loadingIndicatorTitle = context.getString(R.string.sign_in_sign_in_button),
            ) {
                Log.d("plgafhd", "00")
                Log.d("plgafhd", "11")
                val result = credentialManager.getCredential(
                    request = getCredentialRequest,
                    context = activityContext,
                )
                Log.d("plgafhd", "22")
                val loginResult = handleSignIn(result)
                Log.d("plgafhd", "33")
                Log.d("plgafhd", loginResult.toString())
                if (loginResult != null) {
                    Log.d("plgafhd", "44")
                    Log.d("plgafhd", loginResult.toString())
//                    userViewModel.loginGoogle(
//                        loginResult.idToken
//                    )
//                    homeViewModel.refreshData()
//                    navController.navigateAsOrigin(NavigationDestination.Home)
                }
            }
        }
    }

//    val handleGoogleSignIn = {
//
//        coroutineScope.launch {
//            launchSuspendApi(
//                apiOnProgress = apiOnProgress,
//                apiOnError = apiOnError,
//                loadingIndicatorTitle = context.getString(R.string.sign_in_sign_in_button),
//            ) {
//                Log.d("plgafhd","00")
//                val credentialManager = CredentialManager.create(context)
//                Log.d("plgafhd","11")
//                val result = credentialManager.getCredential(
//                    request = request,
//                    context = activityContext,
//                )
//                Log.d("plgafhd","22")
//                val loginResult = handleSignIn(result)
//                Log.d("plgafhd","33")
//                if (loginResult != null) {
//                    userViewModel.loginGoogle(
//                        loginResult.idToken
//                    )
//                    homeViewModel.refreshData()
//                    navController.navigateAsOrigin(NavigationDestination.Home)
//                }
//            }
//        }
//    }

//    fun handleSignIn(result: GetCredentialResponse) {
//        // Handle the successfully returned credential.
//        val credential = result.credential
//
//        when (credential) {
//
//            // Passkey credential
//            is PublicKeyCredential -> {
//                // Share responseJson such as a GetCredentialResponse on your server to
//                // validate and authenticate
//                val responseJson = credential.authenticationResponseJson
//            }
//
//            // Password credential
//            is PasswordCredential -> {
//                // Send ID and password to your server to validate and authenticate.
//                val username = credential.id
//                val password = credential.password
//            }
//
//            // GoogleIdToken credential
//            is CustomCredential -> {
//                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
//                    try {
//                        // Use googleIdTokenCredential and extract id to validate and
//                        // authenticate on your server.
//                        val googleIdTokenCredential = GoogleIdTokenCredential
//                            .createFrom(credential.data)
//                    } catch (e: GoogleIdTokenParsingException) {
//                        Log.e(TAG, "Received an invalid google id token response", e)
//                    }
//                } else {
//                    // Catch any unrecognized custom credential type here.
//                    Log.e(TAG, "Unexpected type of credential")
//                }
//            }
//
//            else -> {
//                // Catch any unrecognized credential type here.
//                Log.e(TAG, "Unexpected type of credential")
//            }
//        }
//    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .background(SNUTTColors.White900),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
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
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            BorderButton(
                modifier = Modifier
                    .fillMaxWidth(),
                color = SNUTTColors.Gray200,
                cornerRadius = 10.dp,
                onClick = { navController.navigate(NavigationDestination.SignIn) },
            ) {
                Text(
                    text = stringResource(R.string.tutorial_sign_in_button),
                    style = SNUTTTypography.button,
                )
            }

            BorderButton(
                modifier = Modifier
                    .fillMaxWidth(),
                color = SNUTTColors.Gray200,
                cornerRadius = 10.dp,
                onClick = { navController.navigate(NavigationDestination.SignUp) },
            ) {
                Text(
                    text = stringResource(R.string.tutorial_sign_up_button),
                    style = SNUTTTypography.button,
                )
            }

            BorderButton(
                modifier = Modifier.fillMaxWidth(),
                color = SNUTTColors.FacebookBlue,
                cornerRadius = 10.dp,
                onClick = { handleFacebookSignIn() },
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
                        style = SNUTTTypography.button,
                    )
                }
            }

            BorderButton(
                modifier = Modifier.fillMaxWidth(),
                color = SNUTTColors.FacebookBlue,
                cornerRadius = 10.dp,
                onClick = { handleGoogleSignIn() },
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.iconfacebook),
                        contentDescription = stringResource(id = R.string.sign_in_sign_in_google_button),
                        modifier = Modifier
                            .height(18.dp)
                            .padding(end = 12.dp),
                    )

                    Text(
                        text = stringResource(R.string.sign_in_sign_in_google_button),
                        color = SNUTTColors.FacebookBlue,
                        style = SNUTTTypography.button,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TutorialPagePreview() {
    TutorialPage()
}
