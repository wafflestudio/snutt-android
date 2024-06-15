package com.wafflestudio.snutt2.views.logged_in.home.settings.theme

import androidx.compose.material.Text
import androidx.compose.ui.res.stringResource
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.ComposableStates
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.views.launchSuspendApi
import kotlinx.coroutines.launch

fun showDeleteThemeDialog(
    composableStates: ComposableStates,
    onConfirm: suspend () -> Unit,
) {
    val modalState = composableStates.modalState
    val context = composableStates.context
    val apiOnError = composableStates.apiOnError
    val apiOnProgress = composableStates.apiOnProgress
    val scope = composableStates.scope

    modalState.set(
        onDismiss = { modalState.hide() },
        onConfirm = {
            scope.launch {
                launchSuspendApi(apiOnProgress, apiOnError) {
                    onConfirm()
                    modalState.hide()
                }
            }
        },
        title = context.getString(R.string.theme_config_dialog_delete_title),
        positiveButton = context.getString(R.string.common_ok),
        negativeButton = context.getString(R.string.common_cancel),
        content = {
            Text(
                text = stringResource(R.string.theme_config_dialog_delete_body),
                style = SNUTTTypography.body1,
            )
        },
    ).show()
}
