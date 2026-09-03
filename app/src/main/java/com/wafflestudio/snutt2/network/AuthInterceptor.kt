package com.wafflestudio.snutt2.network

import com.wafflestudio.snutt2.storage.SNUTTStorage
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    snuttStorage: SNUTTStorage,
) : Interceptor {

    private val accessToken = snuttStorage.accessToken.asStateFlow()

    override fun intercept(chain: Interceptor.Chain): Response {
        val newRequest = chain.request().newBuilder()
            .addHeader("x-access-token", accessToken.value)
            .build()
        return chain.proceed(newRequest)
    }
}
