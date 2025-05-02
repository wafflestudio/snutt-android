package com.wafflestudio.snutt2.lib.network

import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.wafflestudio.snutt2.lib.data.serializer.Serializer
import okhttp3.Interceptor
import okhttp3.Response
import java.nio.charset.StandardCharsets
import javax.inject.Inject
import javax.inject.Singleton

//class GlobalNetworkExceptionInterceptor @Inject constructor(
//    private val globalEventHandler: GlobalEventHandler
//) : Interceptor {
//    override fun intercept(chain: Interceptor.Chain): Response {
//        return try {
//            val response = chain.proceed(chain.request())
//            if (response.code == 401) {
//                globalEventHandler.dispatch(GlobalUiEvent.TokenExpired)
//            }
//            response
//        } catch (e: IOException) {
//            globalEventHandler.dispatch(GlobalUiEvent.NetworkUnavailable)
//            throw e
//        }
//    }
//}

@Singleton
class GlobalNetworkExceptionInterceptor @Inject constructor(
    private val serializer: Serializer,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        try {
            val response = chain.proceed(chain.request())
            val responseBody = (response.body?.run {
                GsonBuilder().setPrettyPrinting().create().toJson(
                    JsonParser.parseString(
                        source().buffer.clone().readString(
                            contentType()?.charset(StandardCharsets.UTF_8) ?: StandardCharsets.UTF_8,
                        ),
                    ),
                )
            } ?:"").also {
                if (!it.contains("errcode"))  return@intercept response
            }

            val errorParsedHttpException = runCatching {
                serializer.deserialize<ErrorDTO>(
                    responseBody,
                    ErrorDTO::class.java,
                )
            }.getOrElse { return@intercept response }

            Log.d("plgafhdtest", errorParsedHttpException.toString())
            return response
        } catch (e: Throwable) {
            Log.d("plgafhdtesterror", e.toString())
            throw e
        }
    }
}
