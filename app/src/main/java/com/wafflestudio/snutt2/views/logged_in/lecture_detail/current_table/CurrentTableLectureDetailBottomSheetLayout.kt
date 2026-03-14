package com.wafflestudio.snutt2.views.logged_in.lecture_detail.current_table

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.domainmodel.LectureSession
import com.wafflestudio.snutt2.domainmodel.SyllabusLecture
import com.wafflestudio.snutt2.lib.android.webview.ReviewWebViewContainer
import com.wafflestudio.snutt2.lib.logging.AnalyticsScreen
import com.wafflestudio.snutt2.lib.logging.DetailScreenReferrer
import com.wafflestudio.snutt2.lib.logging.ReviewDetailParameter
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.views.LocalAnalyticsLogger
import com.wafflestudio.snutt2.views.LocalReviewWebView
import com.wafflestudio.snutt2.views.logged_in.home.reviews.ReviewWebView
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.refactor.DayTimePickerSheetContent

@Composable
fun CurrentTableLectureDetailBottomSheetLayout(
    uiState: CurrentTableLectureDetailUiState,
    sheetState: ModalBottomSheetState,
    onCloseSheet: () -> Unit,
    onEditSessionTime: (Int, LectureSession) -> Unit,
    reviewWebViewContainer: ReviewWebViewContainer,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val analyticsLogger = LocalAnalyticsLogger.current

    LaunchedEffect(sheetState.currentValue) {
        if (sheetState.currentValue == ModalBottomSheetValue.Hidden) {
            onCloseSheet()
        }
    }

    ModalBottomSheetLayout(
        sheetContent = {
            when (val sheetType = uiState.sheetType) {
                CurrentTableLectureDetailUiState.SheetType.None -> {
                    Box(modifier = Modifier.size(1.dp))
                }

                is CurrentTableLectureDetailUiState.SheetType.TimePicker -> {
                    DayTimePickerSheetContent(
                        session = sheetType.session,
                        onDismiss = onCloseSheet,
                        onConfirm = { editedSession ->
                            onEditSessionTime(sheetType.index, editedSession)
                            onCloseSheet()
                        },
                    )
                }

                CurrentTableLectureDetailUiState.SheetType.Review -> {
                    LaunchedEffect(Unit) {
                        analyticsLogger.logScreen(
                            AnalyticsScreen.ReviewDetail(
                                ReviewDetailParameter(
                                    lectureId = when (val lecture = uiState.lecture) {
                                        is SyllabusLecture -> lecture.originalLectureId
                                        else -> lecture.id
                                    },
                                    referrer = DetailScreenReferrer.LectureDetail,
                                ),
                            ),
                        )
                    }
                    // FIXME: 웹뷰 정리할 때 신경쓰기
                    CompositionLocalProvider(LocalReviewWebView provides reviewWebViewContainer) {
                        ReviewWebView(height = 0.95f)
                    }
                }
            }
        },
        sheetState = sheetState,
        sheetShape = RoundedCornerShape(topStartPercent = 5, topEndPercent = 5),
        scrimColor = SNUTTColors.Black.copy(alpha = 0.32f),
        sheetGesturesEnabled = false,
        modifier = modifier,
    ) {
        content()
    }
}
