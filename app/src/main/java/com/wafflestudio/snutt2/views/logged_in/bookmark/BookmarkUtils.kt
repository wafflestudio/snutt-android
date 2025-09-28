package com.wafflestudio.snutt2.views.logged_in.bookmark

import androidx.compose.material.Text
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.ComposableStates
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.views.launchSuspendApi
import kotlinx.coroutines.launch

fun showDialog(
    composableStates: ComposableStates,
    content: String,
    onConfirm: suspend () -> Unit,
) {
    val modalState = composableStates.modalState
    val context = composableStates.context
    val scope = composableStates.scope
    val apiOnError = composableStates.apiOnError
    val apiOnProgress = composableStates.apiOnProgress

    modalState
        .set(
            onDismiss = { modalState.hide() },
            onConfirm = {
                scope.launch {
                    launchSuspendApi(
                        apiOnProgress,
                        apiOnError,
                    ) {
                        onConfirm()
                        modalState.hide()
                    }
                }
            },
            title = context.getString(R.string.notifications_app_bar_title),
            content = {
                Text(
                    text = content,
                    style = SNUTTTypography.body1,
                )
            },
            positiveButton = context.getString(R.string.common_ok),
            negativeButton = context.getString(R.string.common_cancel),
        )
        .show()
}
