package com.wafflestudio.snutt2.lib.android.webview

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.wafflestudio.snutt2.BuildConfig
import com.wafflestudio.snutt2.R
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import java.net.URL

class ThemeMarketWebViewContainer(
    private val context: Context,
    private val accessToken: StateFlow<String?>,
    private val isDarkMode: Boolean,
) : WebViewContainer {
    val loadState: MutableState<LoadState> = mutableStateOf(LoadState.Loading(0))

    override val webView: WebView = WebView(context).apply {
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
                loadState.value = LoadState.Loading(0)
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?,
            ) {
                loadState.value = LoadState.Error
            }
        }
        this.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                when (loadState.value) {
                    is LoadState.Loading -> LoadState.Loading(newProgress)
                    else -> null
                }?.let {
                    loadState.value = it
                }
            }
        }
        this.settings.javaScriptEnabled = true
    }

    override suspend fun openPage(url: String?) {
        val accessToken = accessToken.filterNotNull().first()
        val themeMarketUrl = url ?: THEME_MARKET_URL
        val urlHost = URL(themeMarketUrl).host

        setCookies(urlHost, accessToken)
        webView.loadUrl(themeMarketUrl)
    }

    private fun setCookies(host: String, accessToken: String) {
        CookieManager.getInstance().apply {
            setCookie(
                host,
                "x-access-apikey=${context.getString(R.string.api_key)}",
            )
            setCookie(
                host,
                "x-access-token=$accessToken",
            )
            setCookie(
                host,
                "x-os-type=android",
            )
            setCookie(
                host,
                "x-os-version=${Build.VERSION.SDK_INT}",
            )
            setCookie(
                host,
                "x-app-version=${BuildConfig.VERSION_NAME}",
            )
            setCookie(
                host,
                "x-app-type=${if (BuildConfig.DEBUG) "debug" else "release"}",
            )
            setCookie(
                host,
                "theme=${
                if (isDarkMode) {
                    "dark"
                } else {
                    "light"
                }
                }",
            )
        }.flush()
    }

    companion object {
        private val THEME_MARKET_URL = "https://snutt-theme-market-dev.wafflestudio.com/download"
    }
}
