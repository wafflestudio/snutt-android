package com.wafflestudio.snutt2.feature.syllabus

import android.view.ViewGroup
import android.webkit.WebView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.lib.android.webview.LoadState
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewSurface
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography

@Composable
fun SyllabusWebView(
    syllabusWebViewContainer: SyllabusWebViewContainer,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(SNUTTColors.White900),
    ) {
        when (val loadState = syllabusWebViewContainer.loadState.value) {
            LoadState.Error -> SyllabusWebViewError(
                modifier = Modifier.fillMaxSize(),
                onRetry = { syllabusWebViewContainer.reload() },
            )

            is LoadState.Loading -> SyllabusWebViewLoading(
                modifier = Modifier.fillMaxSize(),
                progress = loadState.progress / 100.0f,
            )

            LoadState.Success -> SyllabusWebViewSuccess(
                modifier = Modifier.fillMaxSize(),
                webView = syllabusWebViewContainer.webView,
            )
        }
    }
}

@Composable
private fun SyllabusWebViewError(modifier: Modifier, onRetry: () -> Unit) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            modifier = Modifier.size(width = 50.dp, height = 58.dp),
            painter = painterResource(id = R.drawable.ic_cat_retry),
            contentDescription = stringResource(R.string.syllabus_webview_error),
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(id = R.string.syllabus_webview_error),
            style = SNUTTTypography.subtitle1,
            color = SNUTTColors.Black900,
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(backgroundColor = SNUTTColors.Sky),
        ) {
            Text(
                text = stringResource(id = R.string.syllabus_webview_error_retry),
                style = SNUTTTypography.h3,
                color = SNUTTColors.White900,
            )
        }
    }
}

@Composable
private fun SyllabusWebViewSuccess(modifier: Modifier, webView: WebView) {
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
private fun SyllabusWebViewLoading(modifier: Modifier, progress: Float) {
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
private fun SyllabusWebViewError_Default() {
    SnuttPreviewSurface {
        SyllabusWebViewError(
            modifier = Modifier.fillMaxSize(),
            onRetry = {},
        )
    }
}

@SnuttPreview
@Composable
private fun SyllabusWebViewLoading_Default() {
    SnuttPreviewSurface {
        SyllabusWebViewLoading(
            modifier = Modifier.fillMaxSize(),
            progress = 0.5f,
        )
    }
}
