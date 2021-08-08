package com.wafflestudio.snutt2.lib.rx

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.*
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.exceptions.OnErrorNotImplementedException
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.plugins.RxJavaPlugins

private val onErrorStub: (Throwable) -> Unit =
    { RxJavaPlugins.onError(OnErrorNotImplementedException(it)) }

interface RxBinder {

    fun Completable.bindUi(
        bindable: RxBindable,
        onError: (Throwable) -> Unit = onErrorStub,
        onComplete: () -> Unit = {}
    ): Disposable {
        return bindable.bindDisposable(
            this.observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onError = onError,
                    onComplete = onComplete
                )
        )
    }

    fun Completable.bindEvent(
        bindable: RxBindable,
        onError: (Throwable) -> Unit = onErrorStub,
        onComplete: () -> Unit = {}
    ): Disposable {
        return bindable.bindDisposable(
            this.subscribeBy(
                onError = onError,
                onComplete = onComplete
            )
        )
    }

    fun <T : Any> Flowable<T>.bindUi(
        bindable: RxBindable,
        onError: (Throwable) -> Unit = onErrorStub,
        onComplete: () -> Unit = {},
        onNext: (T) -> Unit = {}
    ): Disposable {
        return bindable.bindDisposable(
            this.observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onError = onError,
                    onComplete = onComplete,
                    onNext = onNext
                )
        )
    }

    fun <T : Any> Flowable<T>.bindEvent(
        bindable: RxBindable,
        onError: (Throwable) -> Unit = onErrorStub,
        onComplete: () -> Unit = {},
        onNext: (T) -> Unit = {}
    ): Disposable {
        return bindable.bindDisposable(
            this.subscribeBy(
                onError = onError,
                onComplete = onComplete,
                onNext = onNext
            )

        )
    }

    fun <T : Any> Maybe<T>.bindUi(
        bindable: RxBindable,
        onError: (Throwable) -> Unit = onErrorStub,
        onComplete: () -> Unit = {},
        onSuccess: (T) -> Unit = {}
    ): Disposable {
        return bindable.bindDisposable(
            this.observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onError = onError,
                    onComplete = onComplete,
                    onSuccess = onSuccess
                )
        )
    }

    fun <T : Any> Maybe<T>.bindEvent(
        bindable: RxBindable,
        onError: (Throwable) -> Unit = onErrorStub,
        onComplete: () -> Unit = {},
        onSuccess: (T) -> Unit = {}
    ): Disposable {
        return bindable.bindDisposable(
            this.subscribeBy(
                onError = onError,
                onComplete = onComplete,
                onSuccess = onSuccess
            )
        )
    }

    fun <T : Any> Observable<T>.bindUi(
        bindable: RxBindable,
        onError: (Throwable) -> Unit = onErrorStub,
        onComplete: () -> Unit = {},
        onNext: (T) -> Unit = {}
    ): Disposable {
        return bindable.bindDisposable(
            this.observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onError = onError,
                    onComplete = onComplete,
                    onNext = onNext
                )
        )
    }

    fun <T : Any> Observable<T>.bindEvent(
        bindable: RxBindable,
        onError: (Throwable) -> Unit = onErrorStub,
        onComplete: () -> Unit = {},
        onNext: (T) -> Unit = {}
    ): Disposable {
        return bindable.bindDisposable(
            this.subscribeBy(
                onError = onError,
                onComplete = onComplete,
                onNext = onNext
            )
        )
    }

    fun <T : Any> Single<T>.bindUi(
        bindable: RxBindable,
        onError: (Throwable) -> Unit = onErrorStub,
        onSuccess: (T) -> Unit = {}
    ): Disposable {
        return bindable.bindDisposable(
            this.observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onError = onError,
                    onSuccess = onSuccess
                )
        )
    }

    fun <T : Any> Single<T>.bindEvent(
        bindable: RxBindable,
        onError: (Throwable) -> Unit = onErrorStub,
        onSuccess: (T) -> Unit = {}
    ): Disposable {
        return bindable.bindDisposable(
            this.subscribeBy(
                onError = onError,
                onSuccess = onSuccess
            )
        )
    }
}
