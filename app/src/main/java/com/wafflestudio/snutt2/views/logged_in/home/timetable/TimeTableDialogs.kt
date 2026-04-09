package com.wafflestudio.snutt2.views.logged_in.home.timetable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.ui.components.compose.CustomDialog
import com.wafflestudio.snutt2.ui.components.compose.EditText
import com.wafflestudio.snutt2.domain.model.TableSummary

@Composable
fun TimeTableDialogs(
    uiState: TimeTableUiState.Loaded,
    onDismiss: () -> Unit,
    onConfirmChangeTableTitle: (TableSummary, String) -> Unit,
) {
    when (uiState.dialogState) {
        TimeTableUiState.DialogState.None -> {}

        // FIXME: HomeDrawerDialogs 랑 중복 코드
        is TimeTableUiState.DialogState.ChangeTableName -> {
            var newTitle by remember { mutableStateOf(uiState.dialogState.tableSummary.title) }

            CustomDialog(
                onDismiss = onDismiss,
                onConfirm = {
                    onConfirmChangeTableTitle(uiState.dialogState.tableSummary, newTitle)
                },
                title = stringResource(R.string.home_drawer_change_name_dialog_title),
                positiveButtonText = stringResource(R.string.common_ok),
                negativeButtonText = stringResource(R.string.common_cancel),
            ) {
                EditText(value = newTitle, onValueChange = { newTitle = it })
            }
        }
    }
}
