package com.wafflestudio.snutt2.lib.preferences.storage

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.common.collect.Multimap
import com.wafflestudio.snutt2.lib.preferences.serializer.Serializer
import java.lang.reflect.Type

class PrefStorage(
    private val sharedPreferences: SharedPreferences,
    private val serializer: Serializer
) : DataStorage() {

    override fun contains(key: String): Boolean {
        return sharedPreferences.contains(key)
    }

    override fun clearInternal() {
        sharedPreferences.edit().clear().apply()
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> getInternal(key: String, type: Type): T? {
        if (!sharedPreferences.contains(key)) {
            return null
        }

        return when (type) {
            Int::class.java -> sharedPreferences.getInt(key, 0) as T
            Long::class.java -> sharedPreferences.getLong(key, 0L) as T
            Float::class.java -> sharedPreferences.getFloat(key, 0f) as T
            Boolean::class.java -> sharedPreferences.getBoolean(key, false) as T
            String::class.java -> sharedPreferences.getString(key, "") as T
            else -> serializer.deserialize(
                sharedPreferences.getString(key, null) ?: return null,
                type
            )
        }
    }

    override fun <T : Any> putInternal(key: String, value: T?, type: Type) {
        if (value == null) {
            sharedPreferences.edit().remove(key).apply()
            return
        }

        sharedPreferences.edit {
            when (type) {
                Int::class.java -> putInt(key, value as Int)
                Long::class.java -> putLong(key, value as Long)
                Float::class.java -> putFloat(key, value as Float)
                Boolean::class.java -> putBoolean(key, value as Boolean)
                String::class.java -> putString(key, value as String)
                else -> putString(key, serializer.serialize(value, type))
            }
        }
    }
}
