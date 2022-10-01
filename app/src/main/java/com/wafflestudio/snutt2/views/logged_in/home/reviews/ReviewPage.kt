package com.wafflestudio.snutt2.views.logged_in.home.reviews

import android.webkit.WebView
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.TimetableIcon
import com.wafflestudio.snutt2.components.compose.TopBar
import com.wafflestudio.snutt2.lib.android.webview.LoadState
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.views.LocalReviewWebView
import kotlinx.coroutines.launch

@Composable
fun ReviewPage() {
    val context = LocalContext.current
    val webViewContainer = LocalReviewWebView.current
    val onBackPressedDispatcherOwner = LocalOnBackPressedDispatcherOwner.current
    val scope = rememberCoroutineScope()

    val onBackPressedCallback = remember {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (webViewContainer.webView.canGoBack()) {
                    webViewContainer.webView.goBack()
                } else {
//                    homePagerController.update(com.wafflestudio.snutt2.lib.android.HomePage.Timetable)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        if (webViewContainer.loadState.value is LoadState.InitialLoading) {
            webViewContainer.reload(context.getString(R.string.review_base_url))
        }
    }

    DisposableEffect(Unit) {
        onBackPressedDispatcherOwner?.onBackPressedDispatcher?.addCallback(onBackPressedCallback)
        onDispose { onBackPressedCallback.remove() }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        when (val loadState = webViewContainer.loadState.value) {
            LoadState.Error -> WebViewErrorPage(
                modifier = Modifier.fillMaxSize(),
                onRetry = { scope.launch { webViewContainer.reload(null) } }
            )
            is LoadState.InitialLoading -> WebViewLoading(
                modifier = Modifier.fillMaxSize(),
                progress = loadState.progress / 100.0f
            )
            is LoadState.Loading -> WebViewLoading(
                modifier = Modifier.fillMaxSize(),
                progress = loadState.progress / 100.0f
            )
            LoadState.Success -> WebViewSuccess(
                modifier = Modifier.fillMaxSize(),
                webView = webViewContainer.webView
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
                    overflow = TextOverflow.Ellipsis
                )
            },
            navigationIcon = {
                TimetableIcon(
                    modifier = Modifier.size(30.dp),
                    isSelected = true
                )
            }
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
                contentDescription = "네트워크 연결을 확인해주세요."
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = stringResource(id = R.string.reviews_error_message),
                style = SNUTTTypography.subtitle1,
                color = SNUTTColors.Black900
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(backgroundColor = SNUTTColors.Sky)
            ) {
                Text(
                    text = stringResource(id = R.string.reviews_error_retry),
                    style = SNUTTTypography.h3,
                    color = SNUTTColors.White900
                )
            }
        }
    }
}

@Composable
private fun WebViewSuccess(modifier: Modifier, webView: WebView) {
    Column(modifier = modifier.fillMaxSize()) {
        AndroidView(factory = { webView })
    }
}

@Composable
private fun WebViewLoading(modifier: Modifier, progress: Float) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(58.dp))

        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth(),
            progress = progress,
            color = SNUTTColors.Gray200
        )
    }
}

@Preview
@Composable
fun ReviewPagePreview() {
    ReviewPage()
}

