package com.wafflestudio.snutt2.core.database.preference.storage

import com.squareup.moshi.Moshi
import com.wafflestudio.snutt2.core.qualifiers.CoreDatabase
import java.lang.reflect.Type
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@CoreDatabase
class MoshiSerializer @Inject constructor(
    @CoreDatabase private val moshi: Moshi,
) : Serializer {
    override fun <T : Any> deserialize(raw: String, type: Type): T {
        return moshi.adapter<T>(type).fromJson(raw)!!
    }

    override fun <T : Any> serialize(raw: T, type: Type): String {
        return moshi.adapter<T>(type).toJson(raw)
    }
}
