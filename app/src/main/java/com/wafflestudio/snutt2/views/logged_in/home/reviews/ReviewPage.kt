package com.wafflestudio.snutt2.views.logged_in.home.reviews

import android.graphics.Bitmap
import android.webkit.*
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
import com.wafflestudio.snutt2.views.logged_in.home.settings.UserViewModel
import java.net.URL

@Composable
fun ReviewPage() {
    val context = LocalContext.current
    val userViewModel = hiltViewModel<UserViewModel>()

    var accessToken by remember { mutableStateOf("") }
    var tokenReady by remember { mutableStateOf(false) }
    var loadState by remember { mutableStateOf<LoadState>(LoadState.InitialLoading(0)) }

    LaunchedEffect(Unit) {
        accessToken = userViewModel.getAccessToken()
        tokenReady = true
    }

    if (BuildConfig.DEBUG) {
        WebView.setWebContentsDebuggingEnabled(true)
    }

    val webView = remember {
        WebView(context).apply {
            this.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    if (loadState != LoadState.Error) {
                        loadState = LoadState.Success
                    }
                }

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    loadState = when (loadState) {
                        is LoadState.InitialLoading -> LoadState.InitialLoading(0)
                        else -> LoadState.Loading(0)
                    }
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    loadState = LoadState.Error
                }
            }
            this.webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    when (loadState) {
                        is LoadState.InitialLoading -> LoadState.InitialLoading(newProgress)
                        is LoadState.Loading -> LoadState.Loading(newProgress)
                        else -> null
                    }?.let {
                        loadState = it
                    }
                }
            }
            this.settings.javaScriptEnabled = true
        }
    }

    val reload: (String) -> Unit = { url ->
        val reviewUrlHost = URL(context.getString(R.string.review_base_url)).host
        CookieManager.getInstance().apply {
            setCookie(
                reviewUrlHost,
                "x-access-apikey=${context.getString(R.string.api_key)}"
            )
            setCookie(
                reviewUrlHost,
                "x-access-token=$accessToken"
            )
        }.flush()
        webView.loadUrl(url)
    }

    if (loadState == LoadState.InitialLoading(0)) {
        reload(context.getString(R.string.review_base_url))
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (loadState is LoadState.InitialLoading) {
            SimpleTopBar(
                title = stringResource(R.string.reviews_app_bar_title),
                onClickNavigateBack = {
                }
            )
        }
        if (loadState is LoadState.Error) {
        }
        if (tokenReady) {
            AndroidView(factory = { ctx ->
                webView
            })
        }
    }
}

sealed class LoadState {
    object Success : LoadState()
    object Error : LoadState()
    data class Loading(val progress: Int) : LoadState()
    data class InitialLoading(val progress: Int) : LoadState()
}

@Preview
@Composable
fun ReviewPagePreview() {
    ReviewPage()
}
