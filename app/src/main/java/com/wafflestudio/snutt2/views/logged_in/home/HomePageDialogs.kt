package com.wafflestudio.snutt2.views.logged_in.home

import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.ComposableStates
import com.wafflestudio.snutt2.components.compose.EditText
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.views.launchSuspendApi
import kotlinx.coroutines.launch

fun showTitleChangeDialog(
    oldTitle: String,
    tableId: String,
    composableStates: ComposableStates,
    onConfirm: suspend (String, String) -> Unit
) {
    val modalState = composableStates.modalState
    val scope = composableStates.scope
    val context = composableStates.context
    val apiOnError = composableStates.apiOnError
    val apiOnProgress = composableStates.apiOnProgress
    val bottomSheet = composableStates.bottomSheet

    var newTitle by mutableStateOf(oldTitle)
    modalState.set(
        onDismiss = { modalState.hide() },
        onConfirm = {
            scope.launch {
                launchSuspendApi(apiOnProgress, apiOnError) {
                    onConfirm(tableId, newTitle)
                    modalState.hide()
                    bottomSheet.hide()
                }
            }
        },
        title = context.getString(R.string.home_drawer_change_name_dialog_title),
        positiveButton = context.getString(R.string.common_ok),
        negativeButton = context.getString(R.string.common_cancel),
    ) {
        EditText(value = newTitle, onValueChange = { newTitle = it })
    }.show()
}

fun showTableDeleteDialog(
    tableId: String,
    composableStates: ComposableStates,
    onConfirm: suspend (String) -> Unit
) {
    val modalState = composableStates.modalState
    val scope = composableStates.scope
    val context = composableStates.context
    val apiOnError = composableStates.apiOnError
    val apiOnProgress = composableStates.apiOnProgress
    val bottomSheet = composableStates.bottomSheet

    modalState.set(
        onDismiss = { modalState.hide() },
        onConfirm = {
            scope.launch {
                launchSuspendApi(apiOnProgress, apiOnError) {
                    onConfirm(tableId)
                    scope.launch {
                        modalState.hide()
                        bottomSheet.hide()
                    }
                }
            }
        },
        title = context.getString(R.string.home_drawer_table_delete),
        positiveButton = context.getString(R.string.common_ok),
        negativeButton = context.getString(R.string.common_cancel),
    ) {
        Text(stringResource(R.string.table_delete_alert_message), style = SNUTTTypography.body2)
    }.show()
}
