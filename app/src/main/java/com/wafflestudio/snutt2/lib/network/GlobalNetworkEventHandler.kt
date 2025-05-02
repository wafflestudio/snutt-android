package com.wafflestudio.snutt2.lib.network

import java.lang.ref.WeakReference

class GlobalNetworkEventHandler {
    private var mainHandlerRef: WeakReference<(GlobalNetworkEvent) -> Unit>? = null

    fun register(handler: (GlobalNetworkEvent) -> Unit) {
        mainHandlerRef = WeakReference(handler)
    }

    fun dispatch(event: GlobalNetworkEvent) {
        mainHandlerRef?.get()?.invoke(event)
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
