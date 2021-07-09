package com.wafflestudio.snutt2.lib.rx

import com.wafflestudio.snutt2.lib.Optional
import io.reactivex.rxjava3.core.Observable


fun <T : Any> Observable<Optional<T>>.filterEmpty(): Observable<T> {

    return this.filter { it.get() != null }
        .map { it.get()!! }
}
