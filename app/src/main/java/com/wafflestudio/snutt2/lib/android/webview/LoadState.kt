package com.wafflestudio.snutt2.lib.android.webview

sealed class LoadState {
    data object Success : LoadState()
    data object Error : LoadState()
    data class Loading(val progress: Int) : LoadState()
}
