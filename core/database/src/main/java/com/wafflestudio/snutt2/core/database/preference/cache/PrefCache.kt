package com.wafflestudio.snutt2.core.database.preference.cache

interface PrefCache {
    fun clear(domainName: String)
    fun remove(domainName: String, key: String)
    fun put(domainName: String, key: String, value: Any?)
    fun get(domainName: String, key: String): Result

    sealed class Result {
        object Miss : Result()
        data class Hit(val value: Any?) : Result()
    }
}
