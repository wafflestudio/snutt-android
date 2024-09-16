package com.wafflestudio.snutt2.lib.android.webview

sealed class WebViewLoadState {
    object Success : WebViewLoadState()
    object Error : WebViewLoadState()
    data class Loading(val progress: Int) : WebViewLoadState()
    data class InitialLoading(val progress: Int) : WebViewLoadState()
}
