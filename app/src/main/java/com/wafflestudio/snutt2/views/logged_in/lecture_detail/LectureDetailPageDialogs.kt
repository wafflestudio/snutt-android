package com.wafflestudio.snutt2.views.logged_in.lecture_detail

import androidx.compose.material.Text
import androidx.compose.ui.res.stringResource
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.ComposableStates
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.views.launchSuspendApi
import kotlinx.coroutines.launch

fun showDeleteClassTimeDialog(
    composableStates: ComposableStates,
    onConfirm: () -> Unit,
) {
    val modalState = composableStates.modalState
    val context = composableStates.context
    modalState.set(
        title = context.getString(R.string.lecture_detail_delete_class_time_message),
        positiveButton = context.getString(R.string.common_ok),
        negativeButton = context.getString(R.string.common_cancel),
        onDismiss = { modalState.hide() },
        onConfirm = {
            onConfirm()
            modalState.hide()
        },
        content = {},
    ).show()
}

fun showDeleteLectureDialog(
    composableStates: ComposableStates,
    onConfirm: suspend () -> Unit,
) {
    val modalState = composableStates.modalState
    val context = composableStates.context
    val apiOnError = composableStates.apiOnError
    val apiOnProcess = composableStates.apiOnProgress
    val scope = composableStates.scope

    modalState.set(
        title = context.getString(R.string.lecture_detail_delete_dialog_title),
        content = {
            Text(
                text = stringResource(R.string.lecture_detail_delete_dialog_message),
                style = SNUTTTypography.body1
            )
        },
        positiveButton = context.getString(R.string.common_ok),
        negativeButton = context.getString(R.string.common_cancel),
        onDismiss = { modalState.hide() },
        onConfirm = {
            scope.launch {
                launchSuspendApi(apiOnProcess, apiOnError) {
                    onConfirm()
                    modalState.hide()
                }
            }
        }
    ).show()
}

fun showResetLectureDialog(
    composableStates: ComposableStates,
    onConfirm: suspend () -> Unit,
) {
    val modalState = composableStates.modalState
    val context = composableStates.context
    val apiOnError = composableStates.apiOnError
    val apiOnProcess = composableStates.apiOnProgress
    val scope = composableStates.scope

    modalState.set(
        title = context.getString(R.string.lecture_detail_reset_dialog_title),
        content = {
            Text(
                text = stringResource(R.string.lecture_detail_reset_dialog_message),
                style = SNUTTTypography.body2
            )
        },
        positiveButton = context.getString(R.string.common_ok),
        negativeButton = context.getString(R.string.common_cancel),
        onDismiss = { modalState.hide() },
        onConfirm = {
            scope.launch {
                launchSuspendApi(apiOnProcess, apiOnError) {
                    onConfirm()
                    modalState.hide()
                }
            }
        }
    ).show()
}

fun showExitEditModeDialog(
    composableStates: ComposableStates,
    onConfirm: () -> Unit,
) {
    val modalState = composableStates.modalState
    val context = composableStates.context

    modalState.set(
        title = context.getString(R.string.lecture_detail_exit_edit_dialog_message),
        content = {},
        positiveButton = context.getString(R.string.common_ok),
        negativeButton = context.getString(R.string.common_cancel),
        onDismiss = { modalState.hide() },
        onConfirm = {
            onConfirm()
            modalState.hide()
        },
    ).show()
}
