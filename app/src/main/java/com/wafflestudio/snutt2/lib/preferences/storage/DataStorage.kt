package com.wafflestudio.snutt2.lib.preferences.storage

import com.google.common.collect.ArrayListMultimap
import java.lang.reflect.Type

abstract class DataStorage {

    abstract fun contains(key: String): Boolean

    fun <T : Any> get(key: String, type: Type): T? = getInternal(key, type)

    fun <T : Any> put(key: String, value: T?, type: Type) {
        putInternal(key, value, type)
        listeners.get(key).forEach {
            it.onChange(value)
        }
    }

    fun clear() {
        clearInternal()
        listeners.values().forEach {
            it.onChange(null)
        }
    }

    private val listeners = ArrayListMultimap.create<String, KeyValueChangeListener>()

    fun <T : Any> addKeyChangeListener(key: String, listener: KeyValueChangeListener) {
        listeners.put(key, listener)
    }

    fun removeKeyChangeListener(key: String, listener: KeyValueChangeListener) {
        listeners.remove(key, listener)
    }

    interface KeyValueChangeListener {
        fun onChange(value: Any?)
    }


    protected abstract fun <T : Any> getInternal(key: String, type: Type): T?

    protected abstract fun <T : Any> putInternal(key: String, value: T?, type: Type)

    protected abstract fun clearInternal()
}

