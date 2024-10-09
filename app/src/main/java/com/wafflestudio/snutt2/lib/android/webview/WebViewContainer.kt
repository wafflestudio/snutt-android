package com.wafflestudio.snutt2.lib.android.webview

import android.webkit.WebView

interface WebViewContainer {
    val webView: WebView

    suspend fun openPage(url: String?)
}
