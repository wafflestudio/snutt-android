package com.wafflestudio.snutt2.lib.preferences.context

import com.wafflestudio.snutt2.lib.mutableMultiMapOf
import com.wafflestudio.snutt2.lib.preferences.cache.PrefCache
import com.wafflestudio.snutt2.lib.preferences.storage.PrefStorage
import java.lang.reflect.Type

class PrefContext(
    val storage: PrefStorage,
    private val cache: PrefCache,
) {

    data class ChangeListenerMapKey(
        val domainName: String,
        val key: String
    )

    private val listenerMap = mutableMultiMapOf<ChangeListenerMapKey, (Any?) -> Unit>()

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getValue(domainName: String, key: String, type: Type): T? {
        val cacheResult = cache.get(domainName, key)
        if (cacheResult is PrefCache.Result.Hit) {
            return cacheResult.value as? T
        }
        return storage.getValue<T>(domainName, key, type).also {
            cache.put(domainName, key, it)
        }
    }

    fun <T : Any> putValue(
        domainName: String,
        key: String,
        value: T?,
        type: Type
    ) {
        cache.put(domainName, key, value)
        storage.putValue(domainName, key, value, type)
        val listeners = synchronized(listenerMap) {
            listenerMap[ChangeListenerMapKey(domainName, key)]
        }
        listeners.forEach {
            it.invoke(value)
        }
    }

    fun removeValue(domainName: String, key: String) {
        cache.remove(domainName, key)
        storage.removeValue(domainName, key)
        val listeners = synchronized(listenerMap) {
            listenerMap[ChangeListenerMapKey(domainName, key)]
        }
        listeners.forEach {
            it.invoke(null)
        }
    }

    fun clear(domainName: String) {
        cache.clear(domainName)
        storage.clear(domainName)
        val listeners = synchronized(listenerMap) {
            listenerMap.entries
                .filter { it.key.domainName == domainName }
                .map { it.value }
        }
        listeners.forEach {
            it.invoke(null)
        }
    }

    fun addValueChangeListener(domainName: String, key: String, listener: (Any?) -> Unit) {
        synchronized(listenerMap) {
            listenerMap.put(ChangeListenerMapKey(domainName, key), listener)
        }
    }

    fun removeValueChangeListener(domainName: String, key: String, listener: (Any?) -> Unit) {
        synchronized(listenerMap) {
            listenerMap.remove(ChangeListenerMapKey(domainName, key), listener)
        }
    }
}
