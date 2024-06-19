package com.wafflestudio.snutt2.views.logged_out

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.credentials.GetCredentialRequest
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.credentials.CredentialManager
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.BorderButton
import com.wafflestudio.snutt2.lib.facebookLogin
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.views.*
import com.wafflestudio.snutt2.views.logged_in.home.HomeViewModel
import com.wafflestudio.snutt2.views.logged_in.home.settings.UserViewModel
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

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
    val googleAccessToken = userViewModel.googleAccessToken.collectAsState()

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

    /* // TODO : 여기부터
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
    } // TODO : 여기까지 만약 서버측 수정이 가능하다면 쓸 수 있는 코드이다.
     */

    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .requestServerAuthCode(stringResource(R.string.web_client_id)) // Request server auth code
        .build()

    val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(activityContext, gso)

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleSignInResult(
                task = task,
                context = context,
                setGoogleAccessToken = userViewModel::setGoogleAccessToken
            )
        }
    }

    val handleGoogleSignIn = {
        val signInIntent = googleSignInClient.signInIntent
        coroutineScope.launch {
            launcher.launch(signInIntent)
        }
    }

    LaunchedEffect(googleAccessToken) {
        Log.d("plgafhd",googleAccessToken.value)
//        if(googleAccessToken.value != "") { // TODO : 그러므로, 이렇게 구현을 유지한다면 로그아웃시 accesstoken을 초기화하는 과정도 필요하다. 사실 앱을 나갈때나 다른 화면 갈때도 초기화해줘야 한다.
//            coroutineScope.launch {
//                launchSuspendApi(
//                    apiOnProgress = apiOnProgress,
//                    apiOnError = apiOnError,
//                    loadingIndicatorTitle = context.getString(R.string.sign_in_sign_in_button),
//                ) {
//                    if (googleAccessToken.value != "") {
//                        userViewModel.loginGoogle(
//                            googleAccessToken.value
//                        )
//                        homeViewModel.refreshData()
//                        navController.navigateAsOrigin(NavigationDestination.Home)
//                    }
//                }
//            }
//        }
//        test(googleAccessToken.value)
    }

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
                onClick = { googleSignInClient.signOut().addOnCompleteListener {
                    // Start sign-in intent again
                    handleGoogleSignIn()
                } },
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

fun handleSignInResult(task: Task<GoogleSignInAccount>, context: Context, setGoogleAccessToken: (String) -> Unit = { _ -> }) {
    Log.d("GoogleSignIn", "called")
    try {
        val account = task.getResult(ApiException::class.java)
        val authCode = account?.serverAuthCode
        if (authCode != null) {
            exchangeAuthCodeForAccessToken(authCode, context, setGoogleAccessToken)
        }
    } catch (e: ApiException) {
        Log.d("GoogleSignIn", "signInResult:failed code=" + e.statusCode)
    }
}

private fun exchangeAuthCodeForAccessToken(authCode: String, context: Context, setGoogleAccessToken: (String) -> Unit) {
    val clientId = context.getString(R.string.web_client_id)
    val clientSecret = context.getString(R.string.web_client_secret)
    val redirectUri = ""

    val requestBody = FormBody.Builder()
        .add("code", authCode)
        .add("client_id", clientId)
        .add("client_secret", clientSecret)
        .add("redirect_uri", redirectUri)
        .add("grant_type", "authorization_code")
        .build()

    val request = Request.Builder()
        .url("https://oauth2.googleapis.com/token")
        .post(requestBody)
        .build()

    val client = OkHttpClient()
    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            e.printStackTrace()
        }

        override fun onResponse(call: Call, response: Response) {
            if (response.isSuccessful) {
                val responseData = response.body?.string()
                val jsonObject = JSONObject(responseData)
                val accessToken = jsonObject.getString("access_token")
                setGoogleAccessToken(accessToken)
                //test(accessToken)
                // Use the access token
                Log.d("GoogleSignIn", "Access Token: $accessToken")
            } else {
                // Handle error
                Log.e("GoogleSignIn", "Error: " + response.message)
            }
        }
    })
}

@Preview(showBackground = true)
@Composable
fun TutorialPagePreview() {
    TutorialPage()
}
