package com.wafflestudio.snutt2.views.logged_in.home.drawer.refactor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.CustomDialog
import com.wafflestudio.snutt2.components.compose.EditText

@Composable
fun HomeDrawerDialogs(
    uiState: HomeDrawerUiState.Loaded,
    onDismiss: () -> Unit,
    onConfirmChangeTableTitle: (newName: String, tableId: String) -> Unit,
) {
    val context = LocalContext.current

    when (uiState.dialogState) {
        HomeDrawerUiState.DialogState.None -> {}
        is HomeDrawerUiState.DialogState.ChangeTableName -> {
            var newTitle by remember { mutableStateOf(uiState.dialogState.tableSummary.title) }

            CustomDialog(
                onDismiss = onDismiss,
                onConfirm = {
                    onConfirmChangeTableTitle(newTitle, uiState.dialogState.tableSummary.id)
                },
                title = context.getString(R.string.home_drawer_change_name_dialog_title),
                positiveButtonText = context.getString(R.string.common_ok),
                negativeButtonText = context.getString(R.string.common_cancel),
            ) {
                EditText(value = newTitle, onValueChange = { newTitle = it })
            }
        }
    }

}