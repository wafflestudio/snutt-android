package com.wafflestudio.snutt2.core.database.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

fun <T, R> StateFlow<T>.map(scope: CoroutineScope, mapper: (T) -> R): StateFlow<R> {
    return this.map(mapper)
        .stateIn(scope, started = SharingStarted.Eagerly, initialValue = mapper(this.value))
}

fun <T : Any> StateFlow<Optional<T>>.unwrap(scope: CoroutineScope): StateFlow<T?> {
    return this.map(scope) { it.value }
}
