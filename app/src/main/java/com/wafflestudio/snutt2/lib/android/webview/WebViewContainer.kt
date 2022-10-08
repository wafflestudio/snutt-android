package com.wafflestudio.snutt2.lib.android.webview

import android.content.Context
import android.graphics.Bitmap
import android.webkit.*
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.wafflestudio.snutt2.BuildConfig
import com.wafflestudio.snutt2.R
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import java.net.URL

class WebViewContainer(
    private val context: Context,
    private val accessToken: StateFlow<String?>
) {
    val loadState: MutableState<LoadState> = mutableStateOf(LoadState.InitialLoading(0))

    val webView: WebView = WebView(context).apply {
        if (BuildConfig.DEBUG) {
            WebView.setWebContentsDebuggingEnabled(true)
        }
        this.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                if (loadState.value != LoadState.Error) {
                    loadState.value = LoadState.Success
                }
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                loadState.value = when (loadState.value) {
                    is LoadState.InitialLoading -> LoadState.InitialLoading(0)
                    else -> LoadState.Loading(0)
                }
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                loadState.value = LoadState.Error
            }
        }
        this.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                when (loadState.value) {
                    is LoadState.InitialLoading -> LoadState.InitialLoading(newProgress)
                    is LoadState.Loading -> LoadState.Loading(newProgress)
                    else -> null
                }?.let {
                    loadState.value = it
                }
            }
        }
        this.settings.javaScriptEnabled = true
    }

    suspend fun openPage(url: String?) {
        val accessToken = accessToken.filterNotNull().first()
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
        webView.loadUrl(url ?: context.getString(R.string.review_base_url))
    }

    suspend fun reload() {
        val accessToken = accessToken.filterNotNull().first()
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
        webView.reload()
    }
}
