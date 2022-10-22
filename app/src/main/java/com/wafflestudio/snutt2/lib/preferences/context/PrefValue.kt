package com.wafflestudio.snutt2.lib.preferences.context

import com.wafflestudio.snutt2.lib.Optional
import com.wafflestudio.snutt2.lib.data.DataValue
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class PrefValue<T : Any> constructor(
    private val prefContext: PrefContext,
    private val metaData: PrefValueMetaData<T>,
) : DataValue<T> {

    private val asdf = MutableStateFlow(get())

    init {
        val listener: (Any?) -> Unit = { value ->
            asdf.value = ((value as? T) ?: metaData.defaultValue)
        }
        prefContext.addValueChangeListener(metaData.domain, metaData.key, listener)
    }

    @Suppress("UNCHECKED_CAST")
    override fun asObservable(): Observable<T> {
        return Observable.defer<T> {
            val subject = BehaviorSubject.createDefault(get())
            subject.onNext(get())
            val listener: (Any?) -> Unit = { value ->
                subject.onNext((value ?: metaData.defaultValue) as T)
            }
            prefContext.addValueChangeListener(metaData.domain, metaData.key, listener)
            subject
                .doOnDispose {
                    prefContext.removeValueChangeListener(metaData.domain, metaData.key, listener)
                }
        }
            .distinctUntilChanged()
    }

    override fun get(): T {
        return prefContext.getValue(metaData.domain, metaData.key, metaData.type)
            ?: metaData.defaultValue
    }

    override fun update(value: T) {
        prefContext.putValue(metaData.domain, metaData.key, value, metaData.type)
    }

    fun clear() {
        prefContext.removeValue(metaData.domain, metaData.key)
    }

    override fun asStateFlow(): StateFlow<T> {
        return asdf
    }
}
