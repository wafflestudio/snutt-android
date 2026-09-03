package com.wafflestudio.snutt2.network

import android.content.Context
import androidx.core.os.ConfigurationCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.Response
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LanguageInterceptor @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val language = ConfigurationCompat.getLocales(context.resources.configuration)[0]?.language
        val serverLanguage = if (language == Locale.KOREAN.language) KOREAN else ENGLISH

        val newRequest = chain.request().newBuilder()
            .header(HEADER_NAME, serverLanguage)
            .build()
        return chain.proceed(newRequest)
    }

    companion object {
        const val HEADER_NAME = "x-language"

        private const val KOREAN = "ko"
        private const val ENGLISH = "en"
    }
}
