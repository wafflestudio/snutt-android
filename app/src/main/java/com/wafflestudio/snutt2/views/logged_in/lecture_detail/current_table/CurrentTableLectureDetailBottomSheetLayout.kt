package com.wafflestudio.snutt2.views.logged_in.lecture_detail.current_table

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.wafflestudio.snutt2.components.compose.ModalBottomSheetPlaceholder
import com.wafflestudio.snutt2.domainmodel.LectureSession
import com.wafflestudio.snutt2.domainmodel.SyllabusLecture
import com.wafflestudio.snutt2.lib.android.webview.ReviewWebViewContainer
import com.wafflestudio.snutt2.lib.logging.AnalyticsScreen
import com.wafflestudio.snutt2.lib.logging.DetailScreenReferrer
import com.wafflestudio.snutt2.lib.logging.ReviewDetailParameter
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.views.LocalAnalyticsLogger
import com.wafflestudio.snutt2.views.logged_in.home.reviews.ReviewWebViewNew
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
                    ModalBottomSheetPlaceholder()
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
                    ReviewWebViewNew(modifier = Modifier.fillMaxHeight(0.95f), reviewWebViewContainer = reviewWebViewContainer)

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
