package com.wafflestudio.snutt2.lib.network.call_adapter

import com.wafflestudio.snutt2.lib.data.serializer.Serializer
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class ErrorParsingCallAdapterFactory(
    private val delegation: CallAdapter.Factory,
    private val serializer: Serializer
) : CallAdapter.Factory() {

    @Suppress("UNCHECKED_CAST")
    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        if (returnType.javaClass == Call::class.java) {
            val delegationGeneratedCallAdapter =
                delegation.get(returnType, annotations, retrofit) as? CallAdapter<Any, Any>
            return ErrorParsingCallAdapter(
                serializer = serializer,
                delegation = delegationGeneratedCallAdapter,
                // 당장은 Call
                bodyType = (returnType as? ParameterizedType)?.let {
                    it.actualTypeArguments[0]
                } ?: Unit::class.java)
        }
        return null
    }
}
