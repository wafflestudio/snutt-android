package com.wafflestudio.snutt2.lib.rx

import io.reactivex.rxjava3.disposables.Disposable

// Fragment, Activity lifecycle 에 bind
interface RxBindable {
    fun bindDisposable(disposable: Disposable): Disposable
}
