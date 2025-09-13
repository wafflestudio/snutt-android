package com.wafflestudio.snutt2.lib.preferences.context

import com.wafflestudio.snutt2.lib.data.DataValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

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
