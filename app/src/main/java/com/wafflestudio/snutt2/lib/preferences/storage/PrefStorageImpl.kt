package com.wafflestudio.snutt2.lib.preferences.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.wafflestudio.snutt2.lib.data.serializer.Serializer
import java.lang.reflect.Type

class PrefStorageImpl(
    private val context: Context,
    private val serializer: Serializer
) : PrefStorage() {

    override fun <T : Any> getValue(domainName: String, key: String, type: Type): T? {
        getSharedPreference(domainName).let { sharedPreferences ->

            if (!sharedPreferences.contains(key)) {
                return null
            }

            @Suppress("UNCHECKED_CAST")
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
    }

    override fun <T : Any> putValue(domainName: String, key: String, value: T?, type: Type) {
        getSharedPreference(domainName).let { sharedPreferences ->
            if (value == null) {
                removeValue(domainName, key)
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

    override fun removeValue(domainName: String, key: String) {
        getSharedPreference(domainName).edit {
            remove(key)
        }
    }

    override fun clear(domainName: String) {
        getSharedPreference(domainName).edit {
            clear()
        }
    }

    private fun getSharedPreference(domainName: String): SharedPreferences {
        return context.getSharedPreferences(domainName, Context.MODE_PRIVATE)
    }
}
