package com.wafflestudio.snutt2.views.logged_in.home.reviews

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.lib.android.webview.CloseBridge
import com.wafflestudio.snutt2.lib.android.webview.ReviewWebViewContainer
import com.wafflestudio.snutt2.lib.logging.AnalyticsScreen
import com.wafflestudio.snutt2.lib.logging.DetailScreenReferrer
import com.wafflestudio.snutt2.lib.logging.ReviewDetailParameter
import com.wafflestudio.snutt2.lib.logging.logImpression
import com.wafflestudio.snutt2.ui.isDarkMode

@Composable
fun ReviewBottomSheetRoute(
    onNavigateBack: () -> Unit,
    viewModel: ReviewBottomSheetViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val isDarkMode = isDarkMode()

    val reviewWebViewContainer = remember {
        ReviewWebViewContainer(context, viewModel.accessToken, isDarkMode).apply {
            webView.addJavascriptInterface(
                CloseBridge(onClose = { viewModel.close() }),
                "Snutt",
            )
        }
    }

    val url = context.getString(R.string.review_base_url) + "/detail?id=${viewModel.reviewId}&on_back=close"

    LaunchedEffect(Unit) {
        reviewWebViewContainer.openPage(url)
    }

    BackHandler {
        if (reviewWebViewContainer.webView.canGoBack()) {
            reviewWebViewContainer.webView.goBack()
        } else {
            viewModel.close()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                ReviewBottomSheetUiEvent.NavigateBack -> onNavigateBack()
            }
        }
    }

    val referrer = DetailScreenReferrer.decode(viewModel.referrer)

    ReviewWebView(
        modifier = Modifier
            .then(
                if (viewModel.lectureId.isNotEmpty() && referrer != null) {
                    Modifier.logImpression(
                        AnalyticsScreen.ReviewDetail(
                            ReviewDetailParameter(
                                lectureId = viewModel.lectureId,
                                referrer = referrer,
                            ),
                        ),
                    )
                } else {
                    Modifier
                },
            )
            .fillMaxHeight(0.95f),
        reviewWebViewContainer = reviewWebViewContainer,
    )
}
