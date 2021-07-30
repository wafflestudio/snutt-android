package com.wafflestudio.snutt2.lib.preferences.storage

import java.lang.reflect.Type

abstract class PrefStorage {

    abstract fun <T : Any> getValue(domainName: String, key: String, type: Type): T?

    abstract fun <T : Any> putValue(domainName: String, key: String, value: T?, type: Type)

    abstract fun removeValue(domainName: String, key: String)

    abstract fun clear(domainName: String)
}
