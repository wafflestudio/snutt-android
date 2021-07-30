package com.wafflestudio.snutt2.lib

import com.squareup.moshi.JsonClass

fun <T : Any> T?.toOptional(): Optional<T> {
    return Optional.ofNullable(this)
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
