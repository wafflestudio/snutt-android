package com.wafflestudio.snutt2.deeplink

sealed class DeeplinkAction {
    data object DeeplinkNoAction : DeeplinkAction()
    data class DeeplinkNavigationAction(
        val deeplink: String,
    ) : DeeplinkAction()
}
