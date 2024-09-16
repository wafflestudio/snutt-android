package com.wafflestudio.snutt2.components.compose

import android.view.ViewGroup
import android.webkit.WebView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import com.wafflestudio.snutt2.lib.android.webview.WebViewContainer
import com.wafflestudio.snutt2.lib.android.webview.WebViewLoadState
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import kotlinx.coroutines.launch

@Composable
fun SnuttWebView(
    webViewContainer: WebViewContainer,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .background(SNUTTColors.White900),
    ) {
        when (val loadState = webViewContainer.loadState.value) {
            WebViewLoadState.Error -> WebViewError(
                modifier = Modifier.fillMaxSize(),
                onRetry = { scope.launch { webViewContainer.reload() } },
            )

            is WebViewLoadState.InitialLoading -> WebViewLoading(
                modifier = Modifier.fillMaxSize(),
                progress = loadState.progress / 100.0f,
            )

            is WebViewLoadState.Loading -> WebViewLoading(
                modifier = Modifier.fillMaxSize(),
                progress = loadState.progress / 100.0f,
            )

            WebViewLoadState.Success -> WebViewSuccess(
                modifier = Modifier.fillMaxSize(),
                webView = webViewContainer.webView,
            )
        }
    }
}

@Composable
private fun WebViewError(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
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
                TimetableIcon(
                    modifier = Modifier.size(30.dp),
                    isSelected = true,
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
                contentDescription = "네트워크 연결을 확인해주세요.",
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
private fun WebViewSuccess(
    webView: WebView,
    modifier: Modifier = Modifier,
) {
    AndroidView(
        modifier = modifier.fillMaxSize(),
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

@Composable
private fun WebViewLoading(
    progress: Float,
    modifier: Modifier = Modifier,
) {
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
