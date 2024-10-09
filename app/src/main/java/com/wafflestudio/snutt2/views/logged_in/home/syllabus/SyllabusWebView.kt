package com.wafflestudio.snutt2.views.logged_in.home.syllabus

import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.wafflestudio.snutt2.lib.android.webview.WebViewContainer

@Composable
fun SyllabusWebView(syllabusWebViewContainer: WebViewContainer) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.95f),
    ) {
        AndroidView(
            factory = {
                syllabusWebViewContainer.webView.apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                    )
                }
            },
        )
    }
}
