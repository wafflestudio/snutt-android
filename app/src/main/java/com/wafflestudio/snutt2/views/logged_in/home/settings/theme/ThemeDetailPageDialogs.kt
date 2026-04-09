package com.wafflestudio.snutt2.views.logged_in.home.settings.theme

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.CustomDialog
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography

@Composable
fun ThemeDetailDialogContent(
    dialogState: ThemeDetailUiState.DialogState,
    onConfirmCancelEdit: () -> Unit,
    onDismissCancelEdit: () -> Unit,
    onConfirmApplyToTable: () -> Unit,
    onDismissApplyToTable: () -> Unit,
) {
    when (dialogState) {
        ThemeDetailUiState.DialogState.None -> Unit

        ThemeDetailUiState.DialogState.ConfirmCancelEdit -> {
            CustomDialog(
                onDismiss = onDismissCancelEdit,
                onConfirm = onConfirmCancelEdit,
                title = stringResource(R.string.theme_detail_dialog_cancel_edit_title),
                positiveButtonText = stringResource(R.string.common_ok),
                negativeButtonText = stringResource(R.string.common_cancel),
            ) {
                Text(
                    text = stringResource(R.string.theme_detail_dialog_cancel_edit_body),
                    style = SNUTTTypography.body1,
                )
            }
        }

        ThemeDetailUiState.DialogState.ConfirmApplyToTable -> {
            CustomDialog(
                onDismiss = onDismissApplyToTable,
                onConfirm = onConfirmApplyToTable,
                title = stringResource(R.string.theme_detail_dialog_apply_to_current_table_title),
                positiveButtonText = stringResource(R.string.common_yes),
                negativeButtonText = stringResource(R.string.common_no),
            ) {
                Text(
                    text = stringResource(R.string.theme_detail_dialog_apply_to_current_table_body),
                    style = SNUTTTypography.body1,
                )
            }
        }
    }
}
