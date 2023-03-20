package com.wafflestudio.snutt2.views.logged_in.home.search

import android.content.Context
import androidx.compose.material.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.res.stringResource
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.BottomSheet
import com.wafflestudio.snutt2.components.compose.ComposableStates
import com.wafflestudio.snutt2.components.compose.ModalState
import com.wafflestudio.snutt2.lib.android.webview.WebViewContainer
import com.wafflestudio.snutt2.lib.network.ApiOnError
import com.wafflestudio.snutt2.lib.network.ErrorCode
import com.wafflestudio.snutt2.lib.network.ErrorCode.EMAIL_NOT_VERIFIED
import com.wafflestudio.snutt2.lib.network.call_adapter.ErrorParsedHttpException
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.views.LocalReviewWebView
import com.wafflestudio.snutt2.views.launchSuspendApi
import com.wafflestudio.snutt2.views.logged_in.home.reviews.ReviewWebView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun verifyEmailBeforeApi(
    scope: CoroutineScope,
    apiOnError: ApiOnError,
    modalState: ModalState,
    context: Context,
    onUnverified: () -> Unit,
    api: suspend () -> Unit,
) {
    scope.launch {
        try {
            api()
        } catch (e: Exception) {
            when (e) {
                is ErrorParsedHttpException -> {
                    if (e.errorDTO?.code == EMAIL_NOT_VERIFIED) {
                        modalState
                            .set(
                                onDismiss = { modalState.hide() },
                                title = context.getString(R.string.email_unverified_cta_title),
                                positiveButton = context.getString(R.string.common_ok),
                                negativeButton = context.getString(R.string.common_cancel),
                                onConfirm = {
                                    modalState.hide()
                                    onUnverified()
                                }
                            ) {
                                Text(
                                    text = stringResource(R.string.email_unverified_cta_message),
                                    style = SNUTTTypography.button,
                                )
                            }
                            .show()
                    } else apiOnError(e)
                }
                else -> apiOnError(e)
            }
        }
    }
}

suspend fun openReviewBottomSheet(
    url: String?,
    reviewWebViewContainer: WebViewContainer,
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

fun checkLectureOverlap(
    composableStates: ComposableStates,
    onLectureOverlap: (String) -> Unit,
    api: suspend () -> Unit
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
                    } else apiOnError(e)
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
    onForceAdd: suspend () -> Unit,
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
                        apiOnError
                    ) {
                        onForceAdd()
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
                    style = SNUTTTypography.body1
                )
            }
        )
        .show()
}
