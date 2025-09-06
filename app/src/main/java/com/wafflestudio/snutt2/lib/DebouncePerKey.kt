package com.wafflestudio.snutt2.lib

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch

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
        }.apply {
            invokeOnCompletion {
                jobs.remove(key, this)
            }
        }
    }
}
