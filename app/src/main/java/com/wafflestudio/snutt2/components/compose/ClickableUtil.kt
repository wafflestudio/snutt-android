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
import kotlinx.coroutines.flow.channelFlow
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
        interactionSource = remember { MutableInteractionSource() },
    )
}

@Composable
private fun applyEventThrottling(
    event: () -> Unit,
    throttleMs: Long,
): () -> Unit {
    val throttledState = remember {
        MutableSharedFlow<() -> Unit>(
            replay = 0,
            extraBufferCapacity = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST,
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

/**
 * Flow의 각 아이템에 대해 key를 추출하고, 동일한 key를 가진 아이템이 timeoutMillis 안에 연속으로 들어오면 마지막 아이템만 발행합니다.
 * concurrent emission을 지원하는 channelFlow를 사용합니다.
 *
 * @param T Flow가 발행하는 아이템의 타입
 * @param K 중복 제거를 위한 키의 타입
 * @param timeoutMillis 디바운스 시간
 * @param keySelector 아이템에서 키를 추출하는 함수
 */
fun <T, K> Flow<T>.debouncePerKey(
    timeoutMillis: Long,
    keySelector: (T) -> K,
): Flow<T> = channelFlow {
    val jobs = mutableMapOf<K, Job>()

    collect { item ->
        val key = keySelector(item)
        jobs[key]?.cancel()

        jobs[key] = launch {
            delay(timeoutMillis)
            send(item)
        }
    }
}
