package com.wafflestudio.snutt2.lib

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

fun <T, R> StateFlow<T>.map(scope: CoroutineScope, mapper: (T) -> R): StateFlow<R> = this.map(mapper)
    .stateIn(scope, started = SharingStarted.Eagerly, initialValue = mapper(this.value))
