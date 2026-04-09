package com.wafflestudio.snutt2.views.logged_in.home.bookmark

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.ui.components.compose.CustomDialog
import com.wafflestudio.snutt2.domain.model.SearchedLecture
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography

@Composable
fun BookmarkDialogContent(
    dialogState: BookmarkUiState.DialogState,
    onDismiss: () -> Unit,
    onConfirmDeleteBookmark: (SearchedLecture) -> Unit,
    onConfirmDeleteVacancyNotification: (SearchedLecture) -> Unit,
    onConfirmForceAddLecture: (SearchedLecture) -> Unit,
) {
    when (dialogState) {
        BookmarkUiState.DialogState.None -> {
            // No dialog
        }

        is BookmarkUiState.DialogState.DeleteBookmark -> {
            DeleteBookmarkDialog(
                onDismiss = onDismiss,
                onConfirm = { onConfirmDeleteBookmark(dialogState.lecture) },
            )
        }

        is BookmarkUiState.DialogState.DeleteVacancyNotification -> {
            DeleteVacancyNotificationDialog(
                onDismiss = onDismiss,
                onConfirm = { onConfirmDeleteVacancyNotification(dialogState.lecture) },
            )
        }

        is BookmarkUiState.DialogState.LectureTimeOverlap -> {
            LectureTimeOverlapDialog(
                displayMessage = dialogState.displayMessage,
                onDismiss = onDismiss,
                onConfirmForceAddLecture = { onConfirmForceAddLecture(dialogState.lecture) },
            )
        }
    }
}

@Composable
private fun DeleteBookmarkDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    CustomDialog(
        onDismiss = onDismiss,
        onConfirm = onConfirm,
        title = stringResource(R.string.common_notification),
        positiveButtonText = stringResource(R.string.notifications_noti_delete),
        negativeButtonText = stringResource(R.string.common_cancel),
    ) {
        Text(
            text = stringResource(R.string.bookmark_remove_check_message),
            style = SNUTTTypography.body2,
        )
    }
}

@Composable
private fun DeleteVacancyNotificationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    CustomDialog(
        onDismiss = onDismiss,
        onConfirm = onConfirm,
        title = stringResource(R.string.common_notification),
        positiveButtonText = stringResource(R.string.notifications_noti_delete),
        negativeButtonText = stringResource(R.string.common_cancel),
    ) {
        Text(
            text = stringResource(R.string.vacancy_delete_selected_message),
            style = SNUTTTypography.body2,
        )
    }
}

@Composable
private fun LectureTimeOverlapDialog(
    displayMessage: String,
    onDismiss: () -> Unit,
    onConfirmForceAddLecture: () -> Unit,
) {
    CustomDialog(
        onDismiss = onDismiss,
        onConfirm = onConfirmForceAddLecture,
        title = stringResource(R.string.lecture_overlap_error_message),
        positiveButtonText = stringResource(R.string.common_ok),
        negativeButtonText = stringResource(R.string.common_cancel),
    ) {
        Text(
            text = displayMessage,
            style = SNUTTTypography.body2,
        )
    }
}
