package com.wafflestudio.snutt2.views.logged_in.home.reviews

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import com.wafflestudio.snutt2.components.compose.SnuttWebView
import com.wafflestudio.snutt2.lib.android.webview.WebViewContainer
import com.wafflestudio.snutt2.views.LocalHomePageController
import com.wafflestudio.snutt2.views.logged_in.home.HomeItem

@Composable
fun ReviewPage(
    webViewContainer: WebViewContainer,
) {
    val homePageController = LocalHomePageController.current

    val onBackPressed: () -> Unit = {
        if (webViewContainer.webView.canGoBack()) {
            webViewContainer.webView.goBack()
        } else {
            homePageController.update(HomeItem.Timetable)
        }
    }

    BackHandler {
        onBackPressed()
    }

    SnuttWebView(webViewContainer)
}
