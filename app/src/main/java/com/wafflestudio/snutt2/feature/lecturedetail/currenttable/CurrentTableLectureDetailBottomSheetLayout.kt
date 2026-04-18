package com.wafflestudio.snutt2.feature.lecturedetail.currenttable

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.wafflestudio.snutt2.domain.model.LectureSession
import com.wafflestudio.snutt2.feature.lecturedetail.DayTimePickerSheetContent
import com.wafflestudio.snutt2.ui.components.compose.ModalBottomSheetPlaceholder
import com.wafflestudio.snutt2.ui.theme.SNUTTColors

@Composable
fun CurrentTableLectureDetailBottomSheetLayout(
    uiState: CurrentTableLectureDetailUiState,
    sheetState: ModalBottomSheetState,
    onCloseSheet: () -> Unit,
    onEditSessionTime: (Int, LectureSession) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
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
