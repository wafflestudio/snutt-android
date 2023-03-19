package com.wafflestudio.snutt2.views.logged_in.home.search

import androidx.compose.runtime.CompositionLocalProvider
import com.wafflestudio.snutt2.components.compose.BottomSheet
import com.wafflestudio.snutt2.lib.android.webview.WebViewContainer
import com.wafflestudio.snutt2.lib.network.ApiOnError
import com.wafflestudio.snutt2.lib.network.ErrorCode.EMAIL_NOT_VERIFIED
import com.wafflestudio.snutt2.lib.network.call_adapter.ErrorParsedHttpException
import com.wafflestudio.snutt2.views.LocalReviewWebView
import com.wafflestudio.snutt2.views.logged_in.home.reviews.ReviewWebView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun verifyEmailBeforeApi(
    scope: CoroutineScope,
    apiOnError: ApiOnError,
    onUnverified: () -> Unit,
    api: suspend () -> Unit,
) {
    scope.launch {
        try {
            api()
        } catch (e: Exception) {
            when (e) {
                is ErrorParsedHttpException -> {
                    if (e.errorDTO?.code == EMAIL_NOT_VERIFIED) {
                        onUnverified()
                    } else apiOnError(e)
                }
                else -> apiOnError(e)
            }
        }
    }
}

suspend fun openReviewBottomSheet(
    url: String?,
    reviewWebViewContainer: WebViewContainer,
    bottomSheet: BottomSheet,
) {
    reviewWebViewContainer.openPage("$url&on_back=close")
    bottomSheet.setSheetContent {
        CompositionLocalProvider(LocalReviewWebView provides reviewWebViewContainer) {
            ReviewWebView(0.95f)
        }
    }
    bottomSheet.show()
}
