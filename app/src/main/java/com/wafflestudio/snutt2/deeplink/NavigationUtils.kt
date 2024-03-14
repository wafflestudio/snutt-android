package com.wafflestudio.snutt2.deeplink

import androidx.navigation.NavController

fun NavController.popBackStackWithoutQuery() {
    val route = previousBackStackEntry?.destination?.route ?: return
    navigate(route.replace("handled=false", "handled=true")) {
        this.popUpTo(route) {
            inclusive = true
        }
    }
}
