package com.wafflestudio.snutt2.lib.data

import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.flow.StateFlow

interface DataValue<T : Any> : DataProvider<T>, DataUpdater<T>

interface DataProvider<T : Any> {
    fun get(): T

    fun asObservable(): Observable<T>

    fun asStateFlow(): StateFlow<T>
}

interface DataUpdater<T : Any> {
    fun update(value: T)
}
