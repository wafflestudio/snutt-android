package com.wafflestudio.snutt2.feature.diary.diaryhistory

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.domain.model.diary.DiarySummary
import com.wafflestudio.snutt2.ui.components.compose.ConfirmDialog
import com.wafflestudio.snutt2.ui.components.compose.CustomDialog
import com.wafflestudio.snutt2.ui.preview.DiaryPreviewData
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewSurface

@Composable
internal fun DiaryHistoryDialogs(
    uiState: DiaryHistoryUiState,
    onDismissDialog: () -> Unit,
    onConfirmDeleteDiary: (DiarySummary) -> Unit,
    onDismissWriteUnavailableDialog: () -> Unit,
) {
    when (uiState) {
        is DiaryHistoryUiState.Success -> {
            when (val dialogState = uiState.dialogState) {
                DiaryHistoryUiState.DialogState.None -> {}
                is DiaryHistoryUiState.DialogState.DeleteDiary -> {
                    ConfirmDialog(
                        onDismiss = onDismissDialog,
                        onConfirm = { onConfirmDeleteDiary(dialogState.diary) },
                        title = stringResource(R.string.diary_delete_confirm_message, dialogState.diary.courseName),
                    )
                }
            }
        }
        is DiaryHistoryUiState.Empty -> {
            if (uiState.showWriteUnavailableDialog) {
                CustomDialog(
                    onDismiss = onDismissWriteUnavailableDialog,
                    onConfirm = onDismissWriteUnavailableDialog,
                    title = stringResource(R.string.diary_history_empty_no_target_lecture_dialog_title),
                    negativeButtonText = null,
                    content = {},
                )
            }
        }
        DiaryHistoryUiState.Error,
        DiaryHistoryUiState.Loading,
        -> {}
    }
}

@SnuttPreview
@Composable
private fun DiaryHistoryDialogs_DeleteDiary() {
    SnuttPreviewSurface {
        DiaryHistoryDialogs(
            uiState = DiaryHistoryUiState.Success(
                courseBooks = DiaryPreviewData.courseBookList,
                selectedCourseBook = DiaryPreviewData.courseBookList[0],
                diarySummariesByCourseBook = emptyMap(),
                dialogState = DiaryHistoryUiState.DialogState.DeleteDiary(
                    diary = DiaryPreviewData.sampleDiarySummaryShortComment,
                ),
            ),
            onDismissDialog = {},
            onConfirmDeleteDiary = {},
            onDismissWriteUnavailableDialog = {},
        )
    }
}

@SnuttPreview
@Composable
private fun DiaryHistoryDialogs_EmptyWriteUnavailable() {
    SnuttPreviewSurface {
        DiaryHistoryDialogs(
            uiState = DiaryHistoryUiState.Empty(showWriteUnavailableDialog = true),
            onDismissDialog = {},
            onConfirmDeleteDiary = {},
            onDismissWriteUnavailableDialog = {},
        )
    }
}
