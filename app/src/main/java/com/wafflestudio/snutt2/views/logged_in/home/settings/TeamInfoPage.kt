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
import com.wafflestudio.snutt2.BuildConfig
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.SimpleTopBar
import com.wafflestudio.snutt2.ui.ThemeMode
import com.wafflestudio.snutt2.views.LocalNavController

@Composable
fun TeamInfoPage() {
    val navController = LocalNavController.current
    val context = LocalContext.current
    val userViewModel = hiltViewModel<UserViewModel>()
    val webViewClient = WebViewClient()
    val themeMode by userViewModel.themeMode.collectAsState()

    val url = stringResource(R.string.api_server) + stringResource(R.string.member)
    val headers = HashMap<String, String>()
    headers["x-access-apikey"] = BuildConfig.API_KEY
    headers["dark"] = when (themeMode) {
        ThemeMode.DARK -> "dark"
        ThemeMode.LIGHT -> "light"
        ThemeMode.AUTO -> {
            if (isSystemInDarkTheme()) {
                "dark"
            } else {
                "light"
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        SimpleTopBar(
            title = stringResource(R.string.settings_team_info),
            onClickNavigateBack = { navController.popBackStack() },
        )
        AndroidView(factory = {
            WebView(context).apply {
                this.webViewClient = webViewClient
                loadUrl(url, headers)
            }
        })
    }
}

@Preview
@Composable
fun TeamInfoPagePreview() {
    TeamInfoPage()
}
