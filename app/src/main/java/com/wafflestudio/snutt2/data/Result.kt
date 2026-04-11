package com.wafflestudio.snutt2.data

import com.wafflestudio.snutt2.domain.DomainError

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Fail(val error: DomainError) : Result<Nothing>()
}

inline fun <T> Result<T>.onSuccess(action: (T) -> Unit): Result<T> {
    if (this is Result.Success) action(this.data)
    return this
}

inline fun <T> Result<T>.onFailure(action: (DomainError) -> Unit): Result<T> {
    if (this is Result.Fail) action(this.error)
    return this
}
