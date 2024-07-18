package com.wafflestudio.snutt2.di

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.provider.Settings.Secure
import com.squareup.moshi.Moshi
import com.wafflestudio.snutt2.BuildConfig
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.core.qualifiers.App
import com.wafflestudio.snutt2.data.SNUTTStorage
import com.wafflestudio.snutt2.lib.data.serializer.Serializer
import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.lib.network.call_adapter.ErrorParsingCallAdapterFactory
import com.wafflestudio.snutt2.lib.network.createNewNetworkLog
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @SuppressLint("HardwareIds")
    @App
    @Provides
    @Singleton
    fun provideOkHttpClient(
        @ApplicationContext context: Context,
        @App snuttStorage: SNUTTStorage,
    ): OkHttpClient {
        val cache = Cache(File(context.cacheDir, "http"), SIZE_OF_CACHE)
        return OkHttpClient.Builder()
            .cache(cache)
            .addInterceptor { chain ->
                val token = snuttStorage.accessToken.get()
                val newRequest = chain.request().newBuilder()
                    .addHeader(
                        "x-access-token",
                        token,
                    )
                    .build()
                chain.proceed(newRequest)
            }
            .addInterceptor { chain ->
                val newRequest = chain.request().newBuilder()
                    .addHeader(
                        "x-access-apikey",
                        context.getString(R.string.api_key),
                    )
                    .build()
                chain.proceed(newRequest)
            }
            .addInterceptor { chain ->
                val newRequest = chain.request().newBuilder()
                    .addHeader(
                        "x-os-type",
                        "android",
                    )
                    .addHeader(
                        "x-os-version",
                        Build.VERSION.SDK_INT.toString(),
                    )
                    .addHeader(
                        "x-app-version",
                        BuildConfig.VERSION_NAME,
                    )
                    .addHeader(
                        "x-app-type",
                        if (BuildConfig.DEBUG) "debug" else "release",
                    )
                    .addHeader(
                        "x-device-id",
                        Secure.getString(context.contentResolver, Secure.ANDROID_ID),
                    )
                    .addHeader(
                        "x-device-model",
                        listOf(Build.MANUFACTURER.uppercase(), Build.MODEL).joinToString(" "),
                    )
                    .build()
                chain.proceed(newRequest)
            }
            .addInterceptor { chain ->
                val response = chain.proceed(chain.request())
                if (BuildConfig.DEBUG) snuttStorage.addNetworkLog(chain.createNewNetworkLog(context, response))
                response
            }
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = if (BuildConfig.DEBUG) {
                        HttpLoggingInterceptor.Level.BODY
                    } else {
                        HttpLoggingInterceptor.Level.NONE
                    }
                },
            )
            .build()
    }

    @Provides
    @App
    fun provideRetrofit(
        @ApplicationContext context: Context,
        @App okHttpClient: OkHttpClient,
        @App moshi: Moshi,
        @App serializer: Serializer,
    ): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(context.getString(R.string.api_server))
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(
                ErrorParsingCallAdapterFactory(
                    RxJava3CallAdapterFactory.create(),
                    serializer,
                ),
            )
            .build()
    }

    @Provides
    @App
    @Singleton
    fun provideSNUTTRestApi(@App retrofit: Retrofit): SNUTTRestApi {
        return retrofit.create(SNUTTRestApi::class.java)
    }

    private const val SIZE_OF_CACHE = (
        10 * 1024 * 1024 // 10 MB
        ).toLong()

    @Provides
    @App
    @Singleton
    fun provideConnectivityManager(
        @ApplicationContext context: Context,
    ): ConnectivityManager {
        return (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
    }
}
