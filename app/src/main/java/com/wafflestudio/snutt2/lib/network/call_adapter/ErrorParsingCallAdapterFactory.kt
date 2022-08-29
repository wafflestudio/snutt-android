package com.wafflestudio.snutt2.lib.network.call_adapter

import android.util.Log
import com.wafflestudio.snutt2.lib.data.serializer.Serializer
import retrofit2.CallAdapter
import retrofit2.Retrofit
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
        Log.d("___**", "${getRawType(returnType)}")
        val delegationGeneratedCallAdapter =
            delegation.get(returnType, annotations, retrofit) as? CallAdapter<Any, Any>

        return delegationGeneratedCallAdapter?.let {
            ErrorParsingCallAdapter(
                serializer = serializer,
                delegation = delegationGeneratedCallAdapter
            )
        }.also {
            Log.d("___**", "$it")
        }
    }
}
