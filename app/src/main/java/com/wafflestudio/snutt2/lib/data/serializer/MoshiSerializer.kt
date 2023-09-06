package com.wafflestudio.snutt2.lib.data.serializer

import com.squareup.moshi.Moshi
import java.lang.reflect.Type
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MoshiSerializer @Inject constructor(
    private val moshi: Moshi,
) : Serializer {
    override fun <T : Any> deserialize(raw: String, type: Type): T {
        return moshi.adapter<T>(type).fromJson(raw)!!
    }

    override fun <T : Any> serialize(raw: T, type: Type): String {
        return moshi.adapter<T>(type).toJson(raw)
    }
}
