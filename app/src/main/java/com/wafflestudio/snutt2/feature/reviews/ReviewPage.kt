package com.wafflestudio.snutt2.feature.reviews

import android.view.ViewGroup
import android.webkit.WebView
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.lib.android.webview.LoadState
import com.wafflestudio.snutt2.logging.AnalyticsScreen
import com.wafflestudio.snutt2.logging.compose.logImpression
import com.wafflestudio.snutt2.ui.components.compose.SnuttIcon
import com.wafflestudio.snutt2.ui.components.compose.TopBar
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewSurface
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography
import kotlinx.coroutines.launch

@Composable
fun ReviewPage(
    reviewWebViewContainer: ReviewWebViewContainer,
    bottomBar: @Composable () -> Unit,
    onBack: () -> Unit,
) {
    BackHandler {
        if (reviewWebViewContainer.webView.canGoBack()) {
            reviewWebViewContainer.webView.goBack()
        } else {
            onBack()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f)) {
            ReviewWebView(
                reviewWebViewContainer = reviewWebViewContainer,
                modifier = Modifier.logImpression(AnalyticsScreen.ReviewHome),
            )
        }
        bottomBar()
    }
}

@Composable
fun ReviewWebView(
    reviewWebViewContainer: ReviewWebViewContainer,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(SNUTTColors.White900),
    ) {
        when (val loadState = reviewWebViewContainer.loadState.value) {
            LoadState.Error -> WebViewErrorPage(
                modifier = Modifier.fillMaxSize(),
                onRetry = { scope.launch { reviewWebViewContainer.reload() } },
            )

            is LoadState.Loading -> WebViewLoading(
                modifier = Modifier.fillMaxSize(),
                progress = loadState.progress / 100.0f,
            )

            LoadState.Success -> WebViewSuccess(
                modifier = Modifier.fillMaxSize(),
                webView = reviewWebViewContainer.webView,
            )
        }
    }
}

@Composable
private fun WebViewErrorPage(modifier: Modifier, onRetry: () -> Unit) {
    Column(modifier = modifier) {
        TopBar(
            title = {
                Text(
                    text = stringResource(id = R.string.reviews_app_bar_title),
                    style = SNUTTTypography.h2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
            navigationIcon = {
                SnuttIcon(
                    if (true) R.drawable.ic_timetable_selected else R.drawable.ic_timetable_unselected,
                    modifier = Modifier.size(30.dp),

                    colorFilter = ColorFilter.tint(SNUTTColors.Black900),
                )
            },
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                modifier = Modifier.size(width = 50.dp, height = 58.dp),
                painter = painterResource(id = R.drawable.ic_cat_retry),
                contentDescription = stringResource(R.string.reviews_error_content_description),
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = stringResource(id = R.string.reviews_error_message),
                style = SNUTTTypography.subtitle1,
                color = SNUTTColors.Black900,
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(backgroundColor = SNUTTColors.Sky),
            ) {
                Text(
                    text = stringResource(id = R.string.reviews_error_retry),
                    style = SNUTTTypography.h3,
                    color = SNUTTColors.White900,
                )
            }
        }
    }
}

@Composable
private fun WebViewSuccess(modifier: Modifier, webView: WebView) {
    Column(modifier = modifier.fillMaxSize()) {
        AndroidView(
            factory = {
                webView.apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                    )
                }
            },
        )
    }
}

@Composable
private fun WebViewLoading(modifier: Modifier, progress: Float) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Top,
    ) {
        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp),
            progress = progress,
            color = SNUTTColors.Gray200,
        )
    }
}

@SnuttPreview
@Composable
private fun WebViewErrorPage_Default() {
    SnuttPreviewSurface {
        WebViewErrorPage(
            modifier = Modifier.fillMaxSize(),
            onRetry = {},
        )
    }
}

@SnuttPreview
@Composable
private fun WebViewLoading_Default() {
    SnuttPreviewSurface {
        WebViewLoading(
            modifier = Modifier.fillMaxSize(),
            progress = 0.5f,
        )
    }
}
