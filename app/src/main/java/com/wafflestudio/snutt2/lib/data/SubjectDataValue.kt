package com.wafflestudio.snutt2.lib.data

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject

class SubjectDataValue<T : Any> : DataValue<T> {
    private val subject: BehaviorSubject<T>

    constructor() {
        subject = BehaviorSubject.create()
    }

    constructor(initialData: T) {
        subject = BehaviorSubject.createDefault(initialData)
    }

    override fun get(): T {
        return subject.value
    }

    override fun asObservable(): Observable<T> {
        return subject.hide()
    }

    override fun update(value: T) {
        subject.onNext(value)
    }
}
