package com.wafflestudio.snutt2.feature.syllabus

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.ui.util.toast

@Composable
fun SyllabusBottomSheetRoute(
    onNavigateBack: () -> Unit,
    viewModel: SyllabusBottomSheetViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val downloadStartedMessage = stringResource(R.string.syllabus_download_started)

    val syllabusWebViewContainer = remember {
        SyllabusWebViewContainer(
            context = context,
            onDownloadStart = {
                context.toast(downloadStartedMessage)
            },
        )
    }

    LaunchedEffect(Unit) {
        syllabusWebViewContainer.openPage(viewModel.url)
    }

    BackHandler {
        if (syllabusWebViewContainer.webView.canGoBack()) {
            syllabusWebViewContainer.webView.goBack()
        } else {
            onNavigateBack()
        }
    }

    SyllabusWebView(
        modifier = Modifier.fillMaxHeight(0.95f),
        syllabusWebViewContainer = syllabusWebViewContainer,
    )
}
