package com.wafflestudio.snutt2.lib

import com.wafflestudio.snutt2.storage.Optional
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

fun <T, R> StateFlow<T>.map(scope: CoroutineScope, mapper: (T) -> R): StateFlow<R> {
    return this.map(mapper)
        .stateIn(scope, started = SharingStarted.Eagerly, initialValue = mapper(this.value))
}

fun <T : Any> StateFlow<Optional<T>>.unwrap(scope: CoroutineScope): StateFlow<T?> {
    return this.map(scope) { it.value }
}
