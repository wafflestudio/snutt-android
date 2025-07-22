package com.wafflestudio.snutt2.components.compose.snackbar

import androidx.compose.material.SnackbarResult
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.resume

@Stable
class CustomSnackBarHostState {
    private val mutex = Mutex()
    var currentSnackBarData by mutableStateOf<CustomSnackBarStatus>(CustomSnackBarStatus.InVisible)
        private set

    suspend fun showSnackBar(
        message: String,
        actionLabel: String? = null,
        duration: CustomSnackBarDuration,
    ): SnackbarResult =
        mutex.withLock {
            try {
                return suspendCancellableCoroutine { continuation ->
                    currentSnackBarData =
                        CustomSnackBarStatus.FadeInOrBetween(
                            CustomSnackBarDataImpl(message, actionLabel, duration, continuation),
                        )
                }
            } finally {
                currentSnackBarData = CustomSnackBarStatus.FadeOut
            }
        }

    @Stable
    private class CustomSnackBarDataImpl(
        override val message: String,
        override val actionLabel: String?,
        override val duration: CustomSnackBarDuration,
        private val continuation: CancellableContinuation<SnackbarResult>,
    ) : CustomSnackBarData {

        override fun performAction() {
            if (continuation.isActive) continuation.resume(SnackbarResult.ActionPerformed)
        }

        override fun dismiss() {
            if (continuation.isActive) continuation.resume(SnackbarResult.Dismissed)
        }
    }

    @Stable
    class CustomSnackBarDataDefault(
        override val message: String = "",
        override val actionLabel: String? = null,
        override val duration: CustomSnackBarDuration = CustomSnackBarDuration(0L, 0L, 0L),
    ) : CustomSnackBarData {

        override fun performAction() {}

        override fun dismiss() {}
    }
}
