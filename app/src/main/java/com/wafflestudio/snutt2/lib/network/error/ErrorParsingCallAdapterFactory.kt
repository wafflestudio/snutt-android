package com.wafflestudio.snutt2.lib.network.error

import com.wafflestudio.snutt2.lib.data.serializer.Serializer
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.Type

class ErrorParsingCallAdapterFactory(private val serializer: Serializer) : CallAdapter.Factory() {
    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit,
    ): CallAdapter<*, *>? {
        val delegate = retrofit.nextCallAdapter(this, returnType, annotations)
        return if (delegate != null) {
            ErrorParsingCallAdapter(delegate, serializer)
        } else {
            null
        }
    }
}
