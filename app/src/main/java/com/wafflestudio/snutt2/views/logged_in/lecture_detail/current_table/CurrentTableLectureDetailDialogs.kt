package com.wafflestudio.snutt2.views.logged_in.lecture_detail.current_table

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.CustomDialog
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography

@Composable
fun CurrentTableLectureDetailDialogs(
    uiState: CurrentTableLectureDetailUiState,
    onDismiss: () -> Unit,
    onConfirmExitEditMode: () -> Unit,
    onConfirmDeleteSession: () -> Unit,
    onConfirmDeleteLecture: () -> Unit,
    onConfirmResetLecture: () -> Unit,
    onConfirmForceUpdate: () -> Unit,
) {
    when (val dialogState = uiState.dialogState) {
        CurrentTableLectureDetailUiState.DialogState.None -> {}

        CurrentTableLectureDetailUiState.DialogState.ExitEditMode -> {
            CustomDialog(
                onDismiss = onDismiss,
                onConfirm = onConfirmExitEditMode,
                title = stringResource(R.string.lecture_detail_exit_edit_dialog_message),
            ) {}
        }

        is CurrentTableLectureDetailUiState.DialogState.DeleteSession -> {
            CustomDialog(
                onDismiss = onDismiss,
                onConfirm = onConfirmDeleteSession,
                title = stringResource(R.string.lecture_detail_delete_class_time_message),
            ) {}
        }

        CurrentTableLectureDetailUiState.DialogState.DeleteLecture -> {
            CustomDialog(
                onDismiss = onDismiss,
                onConfirm = onConfirmDeleteLecture,
                title = stringResource(R.string.lecture_detail_delete_dialog_title),
            ) {
                Text(
                    text = stringResource(R.string.lecture_detail_delete_dialog_message),
                    style = SNUTTTypography.body1,
                )
            }
        }

        CurrentTableLectureDetailUiState.DialogState.ResetLecture -> {
            CustomDialog(
                onDismiss = onDismiss,
                onConfirm = onConfirmResetLecture,
                title = stringResource(R.string.lecture_detail_reset_dialog_title),
            ) {
                Text(
                    text = stringResource(R.string.lecture_detail_reset_dialog_message),
                    style = SNUTTTypography.body2,
                )
            }
        }

        is CurrentTableLectureDetailUiState.DialogState.LectureTimeOverlap -> {
            CustomDialog(
                onDismiss = onDismiss,
                onConfirm = onConfirmForceUpdate,
                title = stringResource(R.string.lecture_overlap_error_message),
            ) {
                Text(
                    text = dialogState.message,
                    style = SNUTTTypography.body1,
                )
            }
        }
    }
}
