package com.wafflestudio.snutt2.feature.diary.diaryhistory

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.domain.model.diary.DiarySummary
import com.wafflestudio.snutt2.ui.components.compose.ConfirmDialog
import com.wafflestudio.snutt2.ui.preview.DiaryPreviewData
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewSurface

@Composable
internal fun DiaryHistoryDialogs(
    dialogState: DiaryHistoryUiState.DialogState,
    onDismiss: () -> Unit,
    onConfirmDeleteDiary: (DiarySummary) -> Unit,
) {
    when (dialogState) {
        DiaryHistoryUiState.DialogState.None -> {}
        is DiaryHistoryUiState.DialogState.DeleteDiary -> {
            ConfirmDialog(
                onDismiss = onDismiss,
                onConfirm = { onConfirmDeleteDiary(dialogState.diary) },
                title = stringResource(R.string.diary_delete_confirm_message, dialogState.diary.courseName),
            )
        }
    }
}

@SnuttPreview
@Composable
private fun DiaryHistoryDialogs_DeleteDiary() {
    SnuttPreviewSurface {
        DiaryHistoryDialogs(
            dialogState = DiaryHistoryUiState.DialogState.DeleteDiary(
                diary = DiaryPreviewData.sampleDiarySummaryShortComment,
            ),
            onDismiss = {},
            onConfirmDeleteDiary = {},
        )
    }
}
