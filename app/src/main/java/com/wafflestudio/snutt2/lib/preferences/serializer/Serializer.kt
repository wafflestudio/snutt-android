package com.wafflestudio.snutt2.lib.preferences.serializer

import java.lang.reflect.Type

interface Serializer {
    fun <T> serialize(raw: T, type: Type): String
    fun <T> deserialize(raw: String, type: Type): T
}
