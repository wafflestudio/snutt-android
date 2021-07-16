package com.wafflestudio.snutt2.lib.preferences.storage

import com.squareup.moshi.Types
import com.wafflestudio.snutt2.lib.Optional
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import java.lang.reflect.Type
import kotlin.reflect.KClass

class PrefValue<T : Any> private constructor(
    private val key: String,
    private val defaultValue: T,
    private val prefStorage: PrefStorage,
    private val type: Type
) : DataValue<T> {

    @Suppress("UNCHECKED_CAST")
    override fun asObservable(): Observable<T> {
        return Observable.create<T> { emitter ->
            emitter.onNext(getValue())
            val listener = object : DataStorage.KeyValueChangeListener {
                override fun onChange(value: Any?) {
                    emitter.onNext(value as T? ?: defaultValue)
                }
            }
            prefStorage.addKeyChangeListener<T>(key, listener)
            emitter.setDisposable(Disposable.fromAction {
                prefStorage.removeKeyChangeListener(key, listener)
            })
        }
            .distinctUntilChanged()
    }


    override fun getValue(): T {
        return prefStorage.get(key, type) ?: defaultValue
    }

    override fun setValue(value: T) {
        prefStorage.put(key, value, type)
    }

    companion object {
        fun <T : Any> defineNonNullStorageValue(
            key: String,
            defaultValue: T,
            prefStorage: PrefStorage,
            type: KClass<T>
        ): PrefValue<T> {
            return PrefValue(key, defaultValue, prefStorage, type.javaObjectType)
        }

        fun <T : Any> defineNullableStorageValue(
            key: String,
            defaultValue: T?,
            prefStorage: PrefStorage,
            type: KClass<T>
        ): PrefValue<Optional<T>> {
            return PrefValue(
                key,
                Optional.ofNullable(defaultValue),
                prefStorage,
                Types.newParameterizedType(Optional::class.java, type.javaObjectType)
            )
        }

        fun <T : Any> defineListStorageValue(
            key: String,
            defaultValue: List<T>,
            prefStorage: PrefStorage,
            type: KClass<T>
        ): PrefValue<List<T>> {
            return PrefValue(
                key,
                defaultValue,
                prefStorage,
                Types.newParameterizedType(List::class.java, type.javaObjectType)
            )
        }

        fun <T : Any, R : Any> defineMapStorageValue(
            key: String,
            defaultValue: Map<T, R>,
            prefStorage: PrefStorage,
            keyType: KClass<T>,
            valueType: KClass<R>
        ): PrefValue<Map<T, R>> {
            return PrefValue(
                key,
                defaultValue,
                prefStorage,
                Types.newParameterizedType(
                    Map::class.java,
                    keyType.javaObjectType,
                    valueType.javaObjectType
                )
            )
        }
    }
}


