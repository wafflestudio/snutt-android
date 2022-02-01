package com.wafflestudio.snutt2.lib.android

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReviewWebViewUrlStream @Inject constructor() {

    private val _urlStream = PublishSubject.create<String>()
    val urlStream: Observable<String> = _urlStream.hide()

    fun updateUrl(url: String) {
        _urlStream.onNext(url)
    }
}
