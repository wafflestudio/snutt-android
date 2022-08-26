package com.wafflestudio.snutt2.views

import androidx.compose.material.DrawerState
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavController
import com.wafflestudio.snutt2.lib.network.ApiOnError
import com.wafflestudio.snutt2.lib.network.ApiOnProgress

val LocalApiOnError = compositionLocalOf<ApiOnError> {
    throw RuntimeException("")
}

val LocalApiOnProgress = compositionLocalOf<ApiOnProgress> {
    throw RuntimeException("")
}

val LocalNavController = compositionLocalOf<NavController> {
    throw RuntimeException("")
}

val LocalDrawerState = compositionLocalOf<DrawerState> {
    throw RuntimeException("")
}
