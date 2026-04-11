package com.wafflestudio.snutt2.storage

import com.squareup.moshi.JsonClass
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

fun <T : Any> T?.toOptional(): Optional<T> {
    return Optional.ofNullable(this)
}

fun <T : Any> StateFlow<Optional<T>>.unwrap(scope: CoroutineScope): StateFlow<T?> {
    return this.map { it.value }
        .stateIn(scope, started = SharingStarted.Eagerly, initialValue = this.value.value)
}

@JsonClass(generateAdapter = true)
data class Optional<T : Any>(val value: T?) {
    fun get(): T? = value
    fun isDefined(): Boolean = value != null
    fun isEmpty(): Boolean = value == null

    companion object {
        fun <T1 : Any> empty() = Optional<T1>(null)
        fun <T1 : Any> of(value: T1) = Optional(value)
        fun <T1 : Any> ofNullable(value: T1?) = Optional(value)
    }
}
