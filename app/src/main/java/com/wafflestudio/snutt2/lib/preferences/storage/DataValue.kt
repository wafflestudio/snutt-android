package com.wafflestudio.snutt2.lib.preferences.storage

import io.reactivex.rxjava3.core.Observable

interface DataValue<T : Any> {
    fun asObservable(): Observable<T>

    fun getValue(): T

    fun setValue(value: T)
}
