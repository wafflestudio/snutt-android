package com.wafflestudio.snutt2.feature.lecture_detail.current_table

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.wafflestudio.snutt2.ui.components.compose.ModalBottomSheetPlaceholder
import com.wafflestudio.snutt2.domain.model.LectureSession
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.DayTimePickerSheetContent

@Composable
fun AddCustomLectureBottomSheetLayout(
    uiState: AddCustomLectureUiState,
    sheetState: ModalBottomSheetState,
    onCloseSheet: () -> Unit,
    onEditSessionTime: (Int, LectureSession) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    ModalBottomSheetLayout(
        sheetContent = {
            when (val sheetType = uiState.sheetType) {
                AddCustomLectureUiState.SheetType.None -> {
                    ModalBottomSheetPlaceholder()
                }

                is AddCustomLectureUiState.SheetType.TimePicker -> {
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
