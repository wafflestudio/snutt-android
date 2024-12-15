package com.wafflestudio.snutt2.views.logged_in.thememarket

import android.webkit.WebView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.lib.android.webview.LoadState
import com.wafflestudio.snutt2.lib.android.webview.ThemeMarketWebViewContainer
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTheme
import com.wafflestudio.snutt2.ui.SNUTTTypography
import kotlinx.coroutines.launch

@Composable
fun ThemeMarketWebView(
    themeMarketWebViewContainer: ThemeMarketWebViewContainer,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    when (val loadState = themeMarketWebViewContainer.loadState.value) {
        LoadState.Error -> ThemeMarketWebViewError(
            onRetry = {
                scope.launch {
                    themeMarketWebViewContainer.openPage()
                }
            },
            modifier = modifier,
        )
        is LoadState.Loading -> ThemeMarketWebViewLoading(loadState.progress / 100f)
        LoadState.Success -> ThemeMarketWebViewSuccess(themeMarketWebViewContainer.webView)
    }
}

@Composable
private fun ThemeMarketWebViewError(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            modifier = Modifier.size(width = 50.dp, height = 58.dp),
            painter = painterResource(R.drawable.ic_cat_retry),
            contentDescription = "네트워크 연결을 확인해주세요.",
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(R.string.theme_market_webview_error),
            style = SNUTTTypography.subtitle1,
            color = SNUTTColors.Black900,
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(backgroundColor = SNUTTColors.Sky),
        ) {
            Text(
                text = stringResource(id = R.string.theme_market_webview_error_retry),
                style = SNUTTTypography.h3,
                color = SNUTTColors.White900,
            )
        }
    }
}

@Composable
private fun ThemeMarketWebViewLoading(
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

@Composable
private fun ThemeMarketWebViewSuccess(
    webView: WebView,
    modifier: Modifier = Modifier,
) {
    AndroidView(
        factory = {
            webView
        },
        modifier = modifier.clipToBounds(), // Compose에서 WebView 사용 시, WebView가 잠깐 동안 다른 Composable을 가리는 WebView 버그 대응(https://issuetracker.google.com/issues/174233728?pli=1#comment5)
    )
}

@Preview(showBackground = true, heightDp = 640)
@Composable
private fun ThemeMarketWebViewErrorPreview() {
    SNUTTTheme {
        ThemeMarketWebViewError(
            onRetry = {},
        )
    }
}

@Preview(showBackground = true, heightDp = 640)
@Composable
private fun ThemeMarketWebViewLoadingPreview() {
    SNUTTTheme {
        ThemeMarketWebViewLoading(
            progress = 0.5f,
        )
    }
}
