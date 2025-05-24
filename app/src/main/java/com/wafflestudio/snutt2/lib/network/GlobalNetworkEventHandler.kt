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
    ERROR_NETWORK,
    ERROR_SERVER_FAULT,
    ERROR_WRONG_API_KEY,
    ERROR_NO_USER_TOKEN,
    ERROR_WRONG_USER_TOKEN,
    ERROR_NO_ADMIN_PRIVILEGE,
    ERROR_UNKNOWN_APP,
    ERROR_UNKNOWN,
}
