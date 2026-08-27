package com.wafflestudio.snutt2.network

import android.content.Context
import android.content.res.Configuration
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.util.Locale
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class LanguageInterceptorTest {

    @Test
    fun `영어 앱 로케일이면 en 헤더를 주입한다`() {
        val request = interceptRequest(locale = Locale.ENGLISH)

        assertEquals("en", request.header(LanguageInterceptor.HEADER_NAME))
    }

    @Test
    fun `한국어 앱 로케일이면 ko 헤더를 주입한다`() {
        assertEquals("ko", interceptRequest(locale = Locale.KOREAN).header(LanguageInterceptor.HEADER_NAME))
    }

    @Test
    fun `미지원 앱 로케일이면 en 헤더를 주입한다`() {
        assertEquals("en", interceptRequest(locale = Locale.JAPANESE).header(LanguageInterceptor.HEADER_NAME))
    }

    @Test
    fun `기존 x-language 헤더를 단일 현재 언어 값으로 교체한다`() {
        val request = interceptRequest(
            locale = Locale.ENGLISH,
            originalRequest = Request.Builder()
                .url("https://example.com")
                .header(LanguageInterceptor.HEADER_NAME, "ko")
                .build(),
        )

        assertEquals(listOf("en"), request.headers(LanguageInterceptor.HEADER_NAME))
    }

    private fun interceptRequest(
        locale: Locale,
        originalRequest: Request = Request.Builder().url("https://example.com").build(),
    ): Request {
        var interceptedRequest: Request? = null
        OkHttpClient.Builder()
            .addInterceptor(LanguageInterceptor(contextFor(locale)))
            .addInterceptor(
                Interceptor { chain ->
                    interceptedRequest = chain.request()
                    Response.Builder()
                        .request(chain.request())
                        .protocol(Protocol.HTTP_1_1)
                        .code(200)
                        .message("OK")
                        .body("".toResponseBody())
                        .build()
                },
            )
            .build()
            .newCall(originalRequest)
            .execute()
            .close()

        return requireNotNull(interceptedRequest)
    }

    private fun contextFor(locale: Locale): Context {
        val application = RuntimeEnvironment.getApplication()
        val configuration = Configuration(application.resources.configuration).apply {
            setLocale(locale)
        }
        return application.createConfigurationContext(configuration)
    }
}
