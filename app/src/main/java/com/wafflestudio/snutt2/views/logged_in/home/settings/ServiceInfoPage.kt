package com.wafflestudio.snutt2.views.logged_in.home.settings

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.SimpleTopBar
import com.wafflestudio.snutt2.ui.ThemeMode
import com.wafflestudio.snutt2.views.LocalApiOnError
import com.wafflestudio.snutt2.views.LocalApiOnProgress
import com.wafflestudio.snutt2.views.LocalNavController
import com.wafflestudio.snutt2.views.launchSuspendApi
import kotlinx.coroutines.launch

@Composable
fun ServiceInfoPage() {
    val navController = LocalNavController.current
    val context = LocalContext.current
    val apiOnProgress = LocalApiOnProgress.current
    val apiOnError = LocalApiOnError.current
    val scope = rememberCoroutineScope()
    val userViewModel = hiltViewModel<UserViewModel>()
    val webViewClient = WebViewClient()
    val themeMode by userViewModel.themeMode.collectAsState()

    var accessToken: String
    val url = stringResource(R.string.api_server) + stringResource(R.string.terms)
    val headers = HashMap<String, String>()
    headers["x-access-apikey"] = stringResource(R.string.api_key)
    headers["dark"] = when (themeMode) {
        ThemeMode.DARK -> "dark"
        ThemeMode.LIGHT -> "light"
        ThemeMode.AUTO -> {
            if (isSystemInDarkTheme()) "dark"
            else "light"
        }
    }

    var webViewUrlReady by remember { mutableStateOf(false) }

    // FIXME : 다른 형태로 바꾸기
    LaunchedEffect(Unit) {
        launchSuspendApi(apiOnProgress, apiOnError) {
            userViewModel.fetchUserInfo()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        SimpleTopBar(
            title = stringResource(R.string.settings_service_info),
            onClickNavigateBack = { navController.popBackStack() }
        )
        if (webViewUrlReady) {
            AndroidView(factory = {
                WebView(context).apply {
                    this.webViewClient = webViewClient
                    this.loadUrl(url, headers)
                }
            })
        }
        scope.launch {
            accessToken = userViewModel.getAccessToken()
            headers["x-access-token"] = accessToken
            webViewUrlReady = true
        }
    }
}

@Preview
@Composable
fun ServiceInfoPagePreview() {
    ServiceInfoPage()
}
