package com.wafflestudio.snutt2.views.logged_in.home.settings.theme

import androidx.compose.material.Text
import androidx.compose.ui.res.stringResource
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.ComposableStates
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.views.launchSuspendApi
import kotlinx.coroutines.launch

fun showUnsetDefaultDialog(
    composableStates: ComposableStates,
    onConfirm: suspend () -> Unit,
) {
    val modalState = composableStates.modalState
    val context = composableStates.context
    val apiOnError = composableStates.apiOnError
    val apiOnProgress = composableStates.apiOnProgress
    val scope = composableStates.scope

    modalState.setOkCancel(
        context = context,
        onDismiss = { modalState.hide() },
        onConfirm = {
            scope.launch {
                launchSuspendApi(apiOnProgress, apiOnError) {
                    onConfirm()
                    modalState.hide()
                }
            }
        },
        title = context.getString(R.string.theme_detail_dialog_unset_default_title),
        content = {
            Text(
                text = stringResource(R.string.theme_detail_dialog_unset_default_body),
                style = SNUTTTypography.body1,
            )
        },
    ).show()
}

fun showCancelEditDialog(
    composableStates: ComposableStates,
    cancelEdit: suspend () -> Unit,
) {
    val modalState = composableStates.modalState
    val context = composableStates.context
    val apiOnError = composableStates.apiOnError
    val apiOnProgress = composableStates.apiOnProgress
    val scope = composableStates.scope

    modalState.setOkCancel(
        context = context,
        onDismiss = { modalState.hide() },
        onConfirm = {
            scope.launch {
                launchSuspendApi(apiOnProgress, apiOnError) {
                    cancelEdit()
                    modalState.hide()
                }
            }
        },
        title = context.getString(R.string.theme_detail_dialog_cancel_edit_title),
        content = {
            Text(
                text = stringResource(R.string.theme_detail_dialog_cancel_edit_body),
                style = SNUTTTypography.body1,
            )
        },
    ).show()
}

fun showApplyToCurrentTableDialog(
    composableStates: ComposableStates,
    apply: suspend () -> Unit,
    avoid: suspend () -> Unit,
) {
    val modalState = composableStates.modalState
    val context = composableStates.context
    val apiOnError = composableStates.apiOnError
    val apiOnProgress = composableStates.apiOnProgress
    val scope = composableStates.scope

    modalState.setYesNo(
        context = context,
        onDismiss = {
            scope.launch {
                avoid()
                modalState.hide()
            }
        },
        onConfirm = {
            scope.launch {
                launchSuspendApi(apiOnProgress, apiOnError) {
                    apply()
                    modalState.hide()
                }
            }
        },
        title = context.getString(R.string.theme_detail_dialog_apply_to_current_table_title),
        content = {
            Text(
                text = stringResource(R.string.theme_detail_dialog_apply_to_current_table_body),
                style = SNUTTTypography.body1,
            )
        },
    ).show()
}
