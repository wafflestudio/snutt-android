package com.wafflestudio.snutt2.deeplink

import android.net.Uri
import androidx.navigation.NavController

object DeeplinkExecutor {
    fun execute(deeplink: String?, navController: NavController) {
        when (val deeplinkAction = DeeplinkParser.parseDeeplink(deeplink)) {
            DeeplinkAction.DeeplinkNoAction -> {}
            is DeeplinkAction.DeeplinkNavigationAction -> {
                navController.navigate(deeplinkAction.deeplink)
            }
        }
    }
}
