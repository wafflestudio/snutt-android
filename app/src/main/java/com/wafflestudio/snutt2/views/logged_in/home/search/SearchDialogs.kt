package com.wafflestudio.snutt2.views.logged_in.home.search

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.ui.components.compose.CustomDialog
import com.wafflestudio.snutt2.domain.model.SearchedLecture
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography

@Composable
fun SearchDialogs(
    uiState: SearchUiState,
    onDismiss: () -> Unit,
    onConfirmDeleteBookmark: (SearchedLecture) -> Unit,
    onConfirmDeleteVacancy: (SearchedLecture) -> Unit,
    onConfirmAddWithOverlap: (SearchedLecture) -> Unit,
) {
    when (val dialogState = uiState.dialogState) {
        SearchUiState.DialogState.None -> {}

        is SearchUiState.DialogState.ConfirmDeleteBookmark -> {
            CustomDialog(
                onDismiss = onDismiss,
                onConfirm = { onConfirmDeleteBookmark(dialogState.lecture) },
                title = stringResource(R.string.common_notification),
            ) {
                Text(
                    text = stringResource(R.string.bookmark_remove_check_message),
                    style = SNUTTTypography.body1,
                )
            }
        }

        is SearchUiState.DialogState.ConfirmDeleteVacancy -> {
            CustomDialog(
                onDismiss = onDismiss,
                onConfirm = { onConfirmDeleteVacancy(dialogState.lecture) },
                title = stringResource(R.string.common_notification),
            ) {
                Text(
                    text = stringResource(R.string.vacancy_delete_dialog_message),
                    style = SNUTTTypography.body1,
                )
            }
        }

        is SearchUiState.DialogState.ConfirmAddWithOverlap -> {
            CustomDialog(
                onDismiss = onDismiss,
                onConfirm = { onConfirmAddWithOverlap(dialogState.lecture) },
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
