package com.wafflestudio.snutt2.views.logged_in.home.search

import androidx.compose.material.Text
import androidx.compose.runtime.CompositionLocalProvider
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.BottomSheet
import com.wafflestudio.snutt2.components.compose.ComposableStates
import com.wafflestudio.snutt2.lib.android.webview.ReviewWebViewContainer
import com.wafflestudio.snutt2.lib.logging.AnalyticsScreen
import com.wafflestudio.snutt2.lib.logging.DetailScreenReferrer
import com.wafflestudio.snutt2.lib.logging.ReviewDetailParameter
import com.wafflestudio.snutt2.lib.logging.compose.BottomSheetLoggingEffect
import com.wafflestudio.snutt2.lib.network.ErrorCode
import com.wafflestudio.snutt2.lib.network.error.ErrorParsedHttpException
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.views.LocalReviewWebView
import com.wafflestudio.snutt2.views.launchSuspendApi
import com.wafflestudio.snutt2.views.logged_in.home.reviews.ReviewWebView
import kotlinx.coroutines.launch

suspend fun openReviewBottomSheet(
    url: String?,
    reviewWebViewContainer: ReviewWebViewContainer,
    bottomSheet: BottomSheet,
    lectureId: String,
    referrer: DetailScreenReferrer,
) {
    reviewWebViewContainer.openPage("$url&on_back=close")
    bottomSheet.setSheetContent {
        BottomSheetLoggingEffect(
            bottomSheetState = bottomSheet,
            analyticsScreen = AnalyticsScreen.ReviewDetail(
                ReviewDetailParameter(
                    lectureId = lectureId,
                    referrer = referrer,
                ),
            ),
        )
        CompositionLocalProvider(LocalReviewWebView provides reviewWebViewContainer) {
            ReviewWebView(
                height = 0.95f,
            )
        }
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
                    if (e.code == ErrorCode.LECTURE_TIME_OVERLAP) {
                        onLectureOverlap(e.displayMessage ?: "")
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
