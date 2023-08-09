package com.wafflestudio.snutt2.components.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.semantics.Role
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch

fun Modifier.clicks(
    throttleMs: Long = 200L,
    enabled: Boolean = true,
    role: Role? = null,
    onClick: () -> Unit,
) = composed {
    val clickFn = applyEventThrottling(onClick, throttleMs = throttleMs)
    clickable(
        enabled = enabled,
        role = role,
        onClick = clickFn,
        indication = null,
        interactionSource = remember { MutableInteractionSource() }
    )
}

@Composable
private fun applyEventThrottling(
    event: () -> Unit,
    throttleMs: Long
): () -> Unit {
    val throttledState = remember {
        MutableSharedFlow<() -> Unit>(
            replay = 0,
            extraBufferCapacity = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
    }
    LaunchedEffect(true) {
        throttledState
            .throttleFirst(throttleMs)
            .collect { it.invoke() }
    }
    return {
        throttledState.tryEmit(event)
    }
}

private fun <T> Flow<T>.throttleFirst(windowDuration: Long): Flow<T> {
    var job: Job = Job().apply { complete() }
    return onCompletion { job.cancel() }.run {
        flow {
            coroutineScope {
                this@throttleFirst.collect {
                    if (!job.isActive) {
                        emit(it)
                        job = launch { delay(windowDuration) }
                    }
                }
            }
        }
    }
}
