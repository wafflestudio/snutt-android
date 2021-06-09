package com.wafflestudio.snutt2.lib.preferences

import android.content.SharedPreferences
import androidx.core.content.edit
import com.wafflestudio.snutt2.lib.preferences.serializer.Serializer
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.lang.reflect.Type

class PrefStream<T>(
    private val key: String,
    private val defaultValue: T,
    private val sharedPreferences: SharedPreferences,
    private val serializer: Serializer,
    private val type: Type
) {
    private val stream: BehaviorSubject<T> = BehaviorSubject.createDefault(defaultValue)

    // PrefStream 들이 Singleton 하므로 일단 리스너 달아주기만 한다.
    init {
        sharedPreferences.registerOnSharedPreferenceChangeListener { _, changedKey ->
            if (changedKey == this.key) {
                stream.onNext(getValue())
            }
        }
    }


    fun asObservable(): Observable<T> {
        return stream.hide()
    }

    fun getValue(): T =
        sharedPreferences.getString(key, null)?.let { serializer.deserialize<T>(it, type) }
            ?: defaultValue

    fun setValue(value: T) {
        sharedPreferences.edit {
            this.putString(key, serializer.serialize(value, type))
            apply()
        }
    }
}
