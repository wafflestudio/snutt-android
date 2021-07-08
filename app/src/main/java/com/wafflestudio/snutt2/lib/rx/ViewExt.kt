package com.wafflestudio.snutt2.lib.rx

import android.view.View
import com.jakewharton.rxbinding4.view.clicks
import io.reactivex.rxjava3.core.Observable
import java.util.concurrent.TimeUnit

fun View.throttledClicks(): Observable<Unit> {
    return this.clicks()
        .throttleFirst(2000, TimeUnit.MILLISECONDS)
}
