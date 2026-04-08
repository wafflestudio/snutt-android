package com.wafflestudio.snutt2.storage.pref

import kotlinx.coroutines.flow.StateFlow

interface DataValue<T : Any> : DataProvider<T>, DataUpdater<T>

interface DataProvider<T : Any> {
    fun get(): T

    fun asStateFlow(): StateFlow<T>
}

interface DataUpdater<T : Any> {
    fun update(value: T)
}
