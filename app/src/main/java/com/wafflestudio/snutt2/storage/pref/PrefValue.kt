package com.wafflestudio.snutt2.storage.pref

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PrefValue<T : Any> constructor(
    private val prefContext: PrefContext,
    private val metaData: PrefValueMetaData<T>,
) : DataValue<T> {

    private val asdf = MutableStateFlow(get())

    init {
        val listener: (Any?) -> Unit = { value ->
            @Suppress("UNCHECKED_CAST")
            asdf.value = ((value as? T) ?: metaData.defaultValue)
        }
        prefContext.addValueChangeListener(metaData.domain, metaData.key, listener)
    }

    override fun get(): T = prefContext.getValue(metaData.domain, metaData.key, metaData.type)
        ?: metaData.defaultValue

    override fun update(value: T) {
        prefContext.putValue(metaData.domain, metaData.key, value, metaData.type)
    }

    fun clear() {
        prefContext.removeValue(metaData.domain, metaData.key)
    }

    override fun asStateFlow(): StateFlow<T> = asdf
}
