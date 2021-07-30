package com.wafflestudio.snutt2.lib.data

import io.reactivex.rxjava3.core.Observable

interface DataValue<T : Any> {
    fun asObservable(): Observable<T>

    fun get(): T

    fun update(value: T)
}
