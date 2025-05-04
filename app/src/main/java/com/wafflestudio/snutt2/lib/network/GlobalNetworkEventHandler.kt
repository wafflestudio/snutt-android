package com.wafflestudio.snutt2.lib.network

import com.wafflestudio.snutt2.lib.android.runOnUiThread
import java.lang.ref.WeakReference

class GlobalNetworkEventHandler {
    private var networkEventCallbackRef: WeakReference<(GlobalNetworkEvent) -> Unit>? = null

    fun register(handler: (GlobalNetworkEvent) -> Unit) {
        networkEventCallbackRef = WeakReference(handler)
    }

    fun handle(event: GlobalNetworkEvent) {
        // Main thread에서 발생하도록 강제
        runOnUiThread {
            networkEventCallbackRef?.get()?.invoke(event)
        }
    }
}

enum class GlobalNetworkEvent {
    NETWORK_ERROR,
    SERVER_FAULT,
    WRONG_API_KEY,
    NO_USER_TOKEN,
    WRONG_USER_TOKEN,
    NO_ADMIN_PRIVILEGE,
    UNKNOWN_APP,
    UNKNOWN_ERROR,
}
