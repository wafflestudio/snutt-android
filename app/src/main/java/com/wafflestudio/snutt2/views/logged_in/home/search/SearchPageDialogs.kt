package com.wafflestudio.snutt2.views.logged_in.home.search

import androidx.compose.material.Text
import androidx.compose.runtime.CompositionLocalProvider
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.BottomSheet
import com.wafflestudio.snutt2.components.compose.ComposableStates
import com.wafflestudio.snutt2.lib.android.webview.ReviewWebViewContainer
import com.wafflestudio.snutt2.lib.android.webview.WebViewContainer
import com.wafflestudio.snutt2.lib.network.ErrorCode
import com.wafflestudio.snutt2.lib.network.call_adapter.ErrorParsedHttpException
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.views.LocalReviewWebView
import com.wafflestudio.snutt2.views.launchSuspendApi
import com.wafflestudio.snutt2.views.logged_in.home.reviews.ReviewWebView
import com.wafflestudio.snutt2.views.logged_in.home.syllabus.SyllabusWebView
import kotlinx.coroutines.launch

suspend fun openReviewBottomSheet(
    url: String?,
    reviewWebViewContainer: ReviewWebViewContainer,
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

suspend fun openSyllabusBottomSheet(
    url: String?,
    syllabusWebViewContainer: WebViewContainer,
    bottomSheet: BottomSheet,
) {
    syllabusWebViewContainer.openPage(url)
    bottomSheet.setSheetContent {
        SyllabusWebView(syllabusWebViewContainer)
    }
    bottomSheet.show()
}

fun checkLectureOverlap(
    composableStates: ComposableStates,
    onLectureOverlap: (String) -> Unit,
    api: suspend () -> Unit,
) {
    val apiOnProgress = composableStates.apiOnProgress
    val apiOnError = composableStates.apiOnError
    val scope = composableStates.scope

    scope.launch {
        try {
            apiOnProgress.showProgress()
            api()
        } catch (e: Exception) {
            when (e) {
                is ErrorParsedHttpException -> {
                    if (e.errorDTO?.code == ErrorCode.LECTURE_TIME_OVERLAP) {
                        onLectureOverlap(e.errorDTO.ext?.get("confirm_message") ?: "")
                    } else {
                        apiOnError(e)
                    }
                }
                else -> apiOnError(e)
            }
        } finally {
            apiOnProgress.hideProgress()
        }
    }
}

fun showLectureOverlapDialog(
    composableStates: ComposableStates,
    message: String,
    forceAddApi: suspend () -> Unit,
) {
    val modalState = composableStates.modalState
    val scope = composableStates.scope
    val context = composableStates.context
    val apiOnProgress = composableStates.apiOnProgress
    val apiOnError = composableStates.apiOnError

    modalState
        .set(
            onDismiss = { modalState.hide() },
            onConfirm = {
                scope.launch {
                    launchSuspendApi(
                        apiOnProgress,
                        apiOnError,
                    ) {
                        forceAddApi()
                        modalState.hide()
                    }
                }
            },
            title = context.getString(R.string.lecture_overlap_error_message),
            positiveButton = context.getString(R.string.common_ok),
            negativeButton = context.getString(R.string.common_cancel),
            content = {
                Text(
                    text = message,
                    style = SNUTTTypography.body1,
                )
            },
        )
        .show()
}
