package com.wafflestudio.snutt2.lib.rx

import androidx.paging.CombinedLoadStates
import androidx.paging.LoadStates
import androidx.paging.PagingDataAdapter
import com.wafflestudio.snutt2.lib.Optional
import io.reactivex.rxjava3.core.Observable

fun <T : Any> Observable<Optional<T>>.filterEmpty(): Observable<T> {

    return this.filter { it.get() != null }
        .map { it.get()!! }
}

fun PagingDataAdapter<*, *>.loadingState(): Observable<LoadStates> {
    var listener: (CombinedLoadStates) -> Unit = {}

    return Observable.create<LoadStates> { emitter ->
        listener = {
            emitter.onNext(it.source)
        }
        this.addLoadStateListener(listener)
    }
        .doOnDispose {
            this.removeLoadStateListener(listener)
        }
}
