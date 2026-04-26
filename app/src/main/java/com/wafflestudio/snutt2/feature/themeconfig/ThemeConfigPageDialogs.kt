package com.wafflestudio.snutt2.feature.themeconfig

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.domain.model.preview.PreviewData
import com.wafflestudio.snutt2.ui.components.compose.CustomDialog
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewSurface
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography

@Composable
fun ThemeConfigDialogContent(
    dialogState: ThemeConfigUiState.DialogState,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    when (dialogState) {
        ThemeConfigUiState.DialogState.None -> Unit

        is ThemeConfigUiState.DialogState.DeleteTheme -> {
            CustomDialog(
                onDismiss = onDismiss,
                onConfirm = onConfirm,
                title = stringResource(R.string.theme_config_dialog_delete_title),
                positiveButtonText = stringResource(R.string.common_ok),
                negativeButtonText = stringResource(R.string.common_cancel),
            ) {
                Text(
                    text = stringResource(R.string.theme_config_dialog_delete_body),
                    style = SNUTTTypography.body1,
                )
            }
        }
    }
}

@SnuttPreview
@Composable
private fun ThemeConfigDialogContent_DeleteTheme() {
    SnuttPreviewSurface {
        ThemeConfigDialogContent(
            dialogState = ThemeConfigUiState.DialogState.DeleteTheme(PreviewData.previewCustomTheme1),
            onConfirm = {},
            onDismiss = {},
        )
    }
}
