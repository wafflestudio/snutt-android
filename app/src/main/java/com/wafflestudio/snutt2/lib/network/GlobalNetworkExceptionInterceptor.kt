package com.wafflestudio.snutt2.lib.network

import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.wafflestudio.snutt2.lib.data.serializer.Serializer
import okhttp3.Interceptor
import okhttp3.Response
import okio.IOException
import java.nio.charset.StandardCharsets
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GlobalNetworkExceptionInterceptor @Inject constructor(
    private val serializer: Serializer,
    private val globalNetworkEventHandler: GlobalNetworkEventHandler,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        try {
            val response = chain.proceed(chain.request())
            val responseBody = (
                response.body?.run {
                    GsonBuilder().setPrettyPrinting().create().toJson(
                        JsonParser.parseString(
                            source().buffer.clone().readString(
                                contentType()?.charset(StandardCharsets.UTF_8) ?: StandardCharsets.UTF_8,
                            ),
                        ),
                    )
                } ?: ""
                ).also {
                if (!it.contains("errcode")) return@intercept response
            }

            val errorDTO = runCatching {
                serializer.deserialize<ErrorDTO>(
                    responseBody,
                    ErrorDTO::class.java,
                )
            }.getOrElse { return@intercept response }

            when (errorDTO.code) {
                ErrorCode.SERVER_FAULT -> globalNetworkEventHandler.handle(GlobalNetworkEvent.SERVER_FAULT)
                ErrorCode.WRONG_API_KEY -> globalNetworkEventHandler.handle(GlobalNetworkEvent.WRONG_API_KEY)
                ErrorCode.NO_USER_TOKEN -> globalNetworkEventHandler.handle(GlobalNetworkEvent.NO_USER_TOKEN)
                ErrorCode.WRONG_USER_TOKEN -> globalNetworkEventHandler.handle(GlobalNetworkEvent.WRONG_USER_TOKEN)
                ErrorCode.NO_ADMIN_PRIVILEGE -> globalNetworkEventHandler.handle(GlobalNetworkEvent.NO_ADMIN_PRIVILEGE)
                ErrorCode.UNKNOWN_APP -> globalNetworkEventHandler.handle(GlobalNetworkEvent.UNKNOWN_APP)
                else -> {}
            }

            return response
        } catch (e: Throwable) {
            when (e) {
                is IOException -> globalNetworkEventHandler.handle(GlobalNetworkEvent.NETWORK_ERROR)
                is kotlinx.coroutines.CancellationException -> {} // do nothing
                else -> globalNetworkEventHandler.handle(GlobalNetworkEvent.UNKNOWN_ERROR)
            }
            throw e
        }
    }
}
