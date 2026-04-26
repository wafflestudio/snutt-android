package com.wafflestudio.snutt2.feature.themeconfig

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.ui.components.compose.CustomDialog
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewSurface
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography

@Composable
fun ThemeDetailDialogContent(
    dialogState: ThemeDetailUiState.DialogState,
    onConfirmCancelEdit: () -> Unit,
    onDismissCancelEdit: () -> Unit,
    onConfirmApplyToTable: () -> Unit,
    onDismissApplyToTable: () -> Unit,
    onConfirmColorPicker: (Int) -> Unit,
    onDismissColorPicker: () -> Unit,
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

        is ThemeDetailUiState.DialogState.FgColorPicker -> {
            ColorPickerDialog(
                initialColor = Color(dialogState.currentFg),
                onConfirm = onConfirmColorPicker,
                onDismiss = onDismissColorPicker,
            )
        }

        is ThemeDetailUiState.DialogState.BgColorPicker -> {
            ColorPickerDialog(
                initialColor = Color(dialogState.currentBg),
                onConfirm = onConfirmColorPicker,
                onDismiss = onDismissColorPicker,
            )
        }
    }
}

@SnuttPreview
@Composable
private fun ThemeDetailDialogContent_ConfirmCancelEdit() {
    SnuttPreviewSurface {
        ThemeDetailDialogContent(
            dialogState = ThemeDetailUiState.DialogState.ConfirmCancelEdit,
            onConfirmCancelEdit = {},
            onDismissCancelEdit = {},
            onConfirmApplyToTable = {},
            onDismissApplyToTable = {},
            onConfirmColorPicker = {},
            onDismissColorPicker = {},
        )
    }
}

@SnuttPreview
@Composable
private fun ThemeDetailDialogContent_ConfirmApplyToTable() {
    SnuttPreviewSurface {
        ThemeDetailDialogContent(
            dialogState = ThemeDetailUiState.DialogState.ConfirmApplyToTable,
            onConfirmCancelEdit = {},
            onDismissCancelEdit = {},
            onConfirmApplyToTable = {},
            onDismissApplyToTable = {},
            onConfirmColorPicker = {},
            onDismissColorPicker = {},
        )
    }
}
