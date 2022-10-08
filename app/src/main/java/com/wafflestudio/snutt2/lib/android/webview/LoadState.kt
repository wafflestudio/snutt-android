package com.wafflestudio.snutt2.lib.android.webview

sealed class LoadState {
    object Success : LoadState()
    object Error : LoadState()
    data class Loading(val progress: Int) : LoadState()
    data class InitialLoading(val progress: Int) : LoadState()
}
