package com.wafflestudio.snutt2.network

import com.wafflestudio.snutt2.storage.SNUTTStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    snuttStorage: SNUTTStorage,
    externalScope: CoroutineScope,
) : Interceptor {

    private val accessToken = snuttStorage.accessToken.asStateFlow()

    @Volatile
    private var token: String = accessToken.value

    init {
        externalScope.launch {
            accessToken.collect { token = it }
        }
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val newRequest = chain.request().newBuilder()
            .addHeader("x-access-token", token)
            .build()
        return chain.proceed(newRequest)
    }
}
