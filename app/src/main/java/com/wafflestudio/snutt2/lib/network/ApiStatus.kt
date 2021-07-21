package com.wafflestudio.snutt2.lib.network

import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.BehaviorSubject

sealed class ApiStatus<T> {

    data class Success<T>(val data: T) : ApiStatus<T>()

    data class Failure<T>(val error: Throwable) : ApiStatus<T>()

    class Loading<T> : ApiStatus<T>()

    class Default<T> : ApiStatus<T>()
}


fun <T> Single<T>.bindStatus(subject: BehaviorSubject<ApiStatus<T>>): Single<T> {
    return this.doOnSubscribe { subject.onNext(ApiStatus.Loading()) }
        .doOnSuccess { subject.onNext(ApiStatus.Success(it)) }
        .doOnError { subject.onNext(ApiStatus.Failure(it)) }
}
