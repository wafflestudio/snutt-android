package com.wafflestudio.snutt2.di

import android.content.Context
import android.os.Build
import com.squareup.moshi.Moshi
import com.wafflestudio.snutt2.BuildConfig
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.data.SNUTTStorage
import com.wafflestudio.snutt2.lib.data.serializer.Serializer
import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.lib.network.call_adapter.ErrorParsingCallAdapterFactory
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

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideOkHttpClient(
        @ApplicationContext context: Context,
        storage: SNUTTStorage
    ): OkHttpClient {
        val cache = Cache(File(context.cacheDir, "http"), SIZE_OF_CACHE)
        return OkHttpClient.Builder()
            .cache(cache)
            .addInterceptor { chain ->
                val newRequest = chain.request().newBuilder()
                    .addHeader(
                        "x-access-token",
                        storage.accessToken.get()
                    )
                    .build()
                chain.proceed(newRequest)
            }
            .addInterceptor { chain ->
                val newRequest = chain.request().newBuilder()
                    .addHeader(
                        "x-access-apikey",
                        context.getString(R.string.api_key)
                    )
                    .build()
                chain.proceed(newRequest)
            }
            .addInterceptor { chain ->
                val newRequest = chain.request().newBuilder()
                    .addHeader(
                        "x-os-type",
                        "android"
                    )
                    .addHeader(
                        "x-os-version",
                        Build.VERSION.SDK_INT.toString()
                    )
                    .addHeader(
                        "x-app-version",
                        BuildConfig.VERSION_NAME
                    )
                    .addHeader(
                        "x-app-type",
                        if (BuildConfig.DEBUG) "debug" else "release"
                    )
                    .build()
                chain.proceed(newRequest)
            }
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                    else HttpLoggingInterceptor.Level.NONE
                }
            )
            .build()
    }

    @Provides
    fun provideRetrofit(
        @ApplicationContext context: Context,
        okHttpClient: OkHttpClient,
        moshi: Moshi,
        serializer: Serializer
    ): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(context.getString(R.string.api_server))
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(
                ErrorParsingCallAdapterFactory(
                    RxJava3CallAdapterFactory.create(),
                    serializer
                )
            )
            .build()
    }

    @Provides
    fun provideSNUTTRestApi(retrofit: Retrofit): SNUTTRestApi {
        return retrofit.create(SNUTTRestApi::class.java)
    }

    private const val SIZE_OF_CACHE = (
        10 * 1024 * 1024 // 10 MB
        ).toLong()
}
