package com.wafflestudio.snutt2.views

import androidx.compose.material.DrawerState
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavController
import com.wafflestudio.snutt2.lib.android.webview.WebViewContainer
import com.wafflestudio.snutt2.lib.network.ApiOnError
import com.wafflestudio.snutt2.lib.network.ApiOnProgress
import com.wafflestudio.snutt2.views.logged_in.home.HomePageController

val LocalApiOnError = compositionLocalOf<ApiOnError> {
    throw RuntimeException("")
}

val LocalApiOnProgress = compositionLocalOf<ApiOnProgress> {
    throw RuntimeException("")
}

val LocalDrawerState = compositionLocalOf<DrawerState> {
    throw RuntimeException("")
}

val LocalNavController = compositionLocalOf<NavController> {
    throw RuntimeException("")
}

val LocalReviewWebView = compositionLocalOf<WebViewContainer> {
    throw RuntimeException("")
}

val LocalHomePageController = compositionLocalOf<HomePageController> {
    throw RuntimeException("")
}
