package com.wafflestudio.snutt2.lib.preferences.cache

import androidx.collection.LruCache
import com.wafflestudio.snutt2.lib.Optional

class PrefCacheImpl(private val cacheSizePerDomain: Int) : PrefCache {

    private val cacheMap: MutableMap<String, LruCache<String, Optional<Any>>> = mutableMapOf()

    override fun get(domainName: String, key: String): PrefCache.Result {
        synchronized(this) {
            val cachedValue = getCache(domainName).get(key)
            if (cachedValue != null) {
                @Suppress("UNCHECKED_CAST")
                return PrefCache.Result.Hit(cachedValue.value)
            }
            return PrefCache.Result.Miss
        }
    }

    override fun put(domainName: String, key: String, value: Any?) {
        synchronized(this) {
            getCache(domainName).put(key, Optional(value))
        }
    }

    override fun remove(domainName: String, key: String) {
        synchronized(this) {
            getCache(domainName).put(key, Optional(null))
        }
    }

    override fun clear(domainName: String) {
        synchronized(this) {
            cacheMap.remove(domainName)
        }
    }

    private fun getCache(domainName: String): LruCache<String, Optional<Any>> {
        return cacheMap[domainName] ?: LruCache<String, Optional<Any>>(cacheSizePerDomain).also {
            cacheMap[domainName] = it
        }
    }
}
