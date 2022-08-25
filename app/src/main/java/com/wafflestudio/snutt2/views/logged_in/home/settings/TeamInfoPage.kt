package com.wafflestudio.snutt2.views.logged_in.home.settings

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.ArrowBackIcon
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.views.NavControllerContext
import kotlinx.coroutines.launch

@Composable
fun TeamInfoPage() {
    val navController = NavControllerContext.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val userViewModel = hiltViewModel<UserViewModel>()
    val webViewClient = WebViewClient()

    var accessToken: String
    val url = stringResource(R.string.api_server) + stringResource(R.string.member)
    val headers = HashMap<String, String>()
    headers["x-access-apikey"] = stringResource(R.string.api_key)

    var webViewUrlReady by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(text = stringResource(R.string.settings_team_info)) },
            navigationIcon = {
                ArrowBackIcon(
                    modifier = Modifier
                        .size(30.dp)
                        .clicks {
                            navController.popBackStack()
                        }
                )
            }
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
fun TeamInfoPagePreview() {
    TeamInfoPage()
}
