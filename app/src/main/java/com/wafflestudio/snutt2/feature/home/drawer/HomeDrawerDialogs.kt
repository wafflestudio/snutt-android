package com.wafflestudio.snutt2.feature.home.drawer

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.domain.model.TableSummary
import com.wafflestudio.snutt2.ui.components.compose.CustomDialog
import com.wafflestudio.snutt2.ui.components.compose.EditText
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewSurface
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography

/**
 * 논의
 * 다이얼로그 또한 바텀시트처럼 Route 별로 ${Feature}Dialogs.kt 로 묶은 뒤 Screen 단에서 렌더링해주기.
 * 역시 uiState의 필드로 sealed class DialogState 를 두고, 여기에서 분기해서 그려주기.
 * 다이얼로그 컴포저블 별로 파일 모두 분리해도 괜찮을듯 (폴더 한 뎁스 더 두고)
 */
@Composable
fun HomeDrawerDialogs(
    uiState: HomeDrawerUiState,
    onDismiss: () -> Unit,
    onChangeTableNameTitleChange: (String) -> Unit,
    onConfirmChangeTableTitle: () -> Unit,
    onConfirmDeleteTable: (tableSummary: TableSummary) -> Unit,
) {
    when (uiState.dialogState) {
        HomeDrawerUiState.DialogState.None -> {}
        is HomeDrawerUiState.DialogState.ChangeTableName -> {
            CustomDialog(
                onDismiss = onDismiss,
                onConfirm = onConfirmChangeTableTitle,
                title = stringResource(R.string.home_drawer_change_name_dialog_title),
                positiveButtonText = stringResource(R.string.common_ok),
                negativeButtonText = stringResource(R.string.common_cancel),
            ) {
                EditText(
                    value = uiState.dialogState.newTitle,
                    onValueChange = onChangeTableNameTitleChange,
                )
            }
        }

        is HomeDrawerUiState.DialogState.DeleteTable -> {
            CustomDialog(
                onDismiss = onDismiss,
                onConfirm = {
                    onConfirmDeleteTable(uiState.dialogState.tableSummary)
                },
                title = stringResource(R.string.home_drawer_table_delete),
                positiveButtonText = stringResource(R.string.common_ok),
                negativeButtonText = stringResource(R.string.common_cancel),
            ) {
                Text(
                    stringResource(R.string.table_delete_alert_message),
                    style = SNUTTTypography.body2,
                )
            }
        }
    }
}

@SnuttPreview
@Composable
private fun HomeDrawerDialogs_ChangeTableName() {
    SnuttPreviewSurface {
        HomeDrawerDialogs(
            uiState = HomeDrawerUiState(
                dialogState = HomeDrawerUiState.DialogState.ChangeTableName(
                    tableSummary = TableSummary.Default,
                ),
            ),
            onDismiss = {},
            onChangeTableNameTitleChange = {},
            onConfirmChangeTableTitle = {},
            onConfirmDeleteTable = {},
        )
    }
}

@SnuttPreview
@Composable
private fun HomeDrawerDialogs_DeleteTable() {
    SnuttPreviewSurface {
        HomeDrawerDialogs(
            uiState = HomeDrawerUiState(
                dialogState = HomeDrawerUiState.DialogState.DeleteTable(
                    tableSummary = TableSummary.Default,
                ),
            ),
            onDismiss = {},
            onChangeTableNameTitleChange = {},
            onConfirmChangeTableTitle = {},
            onConfirmDeleteTable = {},
        )
    }
}
