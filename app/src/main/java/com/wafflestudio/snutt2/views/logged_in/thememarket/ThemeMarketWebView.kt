package com.wafflestudio.snutt2.views.logged_in.thememarket

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.viewinterop.AndroidView
import com.wafflestudio.snutt2.lib.android.webview.WebViewContainer

@Composable
fun ThemeMarketWebView(
    themeMarketWebViewContainer: WebViewContainer,
    modifier: Modifier = Modifier,
) {
    AndroidView(
        factory = {
            themeMarketWebViewContainer.webView
        },
        modifier = modifier.clipToBounds(), // Compose에서 WebView 사용 시, WebView가 잠깐 동안 다른 Composable을 가리는 WebView 버그 대응(https://issuetracker.google.com/issues/174233728?pli=1#comment5)
    )
}
