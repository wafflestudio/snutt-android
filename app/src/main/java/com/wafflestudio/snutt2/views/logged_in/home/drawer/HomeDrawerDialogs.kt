package com.wafflestudio.snutt2.views.logged_in.home.drawer

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.ui.components.compose.CustomDialog
import com.wafflestudio.snutt2.ui.components.compose.EditText
import com.wafflestudio.snutt2.domain.model.TableSummary
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
    onConfirmChangeTableTitle: (TableSummary, String) -> Unit,
    onConfirmDeleteTable: (tableSummary: TableSummary) -> Unit,
) {
    val context = LocalContext.current

    when (uiState.dialogState) {
        HomeDrawerUiState.DialogState.None -> {}
        is HomeDrawerUiState.DialogState.ChangeTableName -> {
            var newTitle by remember { mutableStateOf(uiState.dialogState.tableSummary.title) }

            CustomDialog(
                onDismiss = onDismiss,
                onConfirm = {
                    onConfirmChangeTableTitle(uiState.dialogState.tableSummary, newTitle)
                },
                title = context.getString(R.string.home_drawer_change_name_dialog_title),
                positiveButtonText = context.getString(R.string.common_ok),
                negativeButtonText = context.getString(R.string.common_cancel),
            ) {
                EditText(value = newTitle, onValueChange = { newTitle = it })
            }
        }

        is HomeDrawerUiState.DialogState.DeleteTable -> {
            CustomDialog(
                onDismiss = onDismiss,
                onConfirm = {
                    onConfirmDeleteTable(uiState.dialogState.tableSummary)
                },
                title = context.getString(R.string.home_drawer_table_delete),
                positiveButtonText = context.getString(R.string.common_ok),
                negativeButtonText = context.getString(R.string.common_cancel),
            ) {
                Text(
                    stringResource(R.string.table_delete_alert_message),
                    style = SNUTTTypography.body2,
                )
            }
        }
    }
}
