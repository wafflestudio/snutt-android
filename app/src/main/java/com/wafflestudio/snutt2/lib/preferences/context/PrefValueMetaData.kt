package com.wafflestudio.snutt2.lib.preferences.context

import com.squareup.moshi.Types
import com.wafflestudio.snutt2.lib.Optional
import java.lang.reflect.Type

open class PrefValueMetaData<T : Any> constructor(
    val domain: String,
    val key: String,
    val type: Type,
    val defaultValue: T
)

class PrefListValueMetaData<T : Any> constructor(
    domain: String,
    key: String,
    type: Type,
    defaultValue: List<T>
) : PrefValueMetaData<List<T>>(
    domain,
    key,
    Types.newParameterizedType(List::class.java, type),
    defaultValue
)

class PrefOptionalValueMetaData<T : Any> constructor(
    domain: String,
    key: String,
    type: Type,
    defaultValue: Optional<T>
) : PrefValueMetaData<Optional<T>>(
    domain,
    key,
    Types.newParameterizedType(Optional::class.java, type),
    defaultValue
)

class PrefMapValueMetaData<TKey : Any, TValue : Any> constructor(
    domain: String,
    key: String,
    keyType: Type,
    valueType: Type,
    defaultValue: Map<TKey, TValue>
) : PrefValueMetaData<Map<TKey, TValue>>(
    domain,
    key,
    Types.newParameterizedType(Map::class.java, keyType, valueType),
    defaultValue
)
