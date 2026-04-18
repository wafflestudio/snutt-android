package com.wafflestudio.snutt2.di

import android.annotation.SuppressLint
import android.content.Context
import com.squareup.moshi.Moshi
import com.wafflestudio.snutt2.BuildConfig
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.lib.android.createNewNetworkLog
import com.wafflestudio.snutt2.network.api.google.SNUTTRestApiForGoogle
import com.wafflestudio.snutt2.storage.SNUTTStorage
import com.wafflestudio.snutt2.storage.addNetworkLog
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModuleForGoogle {

    @SuppressLint("HardwareIds")
    @Provides
    @Singleton
    fun provideOkHttpClientForGoogle(
        @ApplicationContext context: Context,
        snuttStorage: SNUTTStorage,
    ): OkHttpClient {
        val cache = Cache(File(context.cacheDir, "http"), SIZE_OF_CACHE)
        return OkHttpClient.Builder()
            .cache(cache)
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
    fun provideRetrofitForGoogle(
        @ApplicationContext context: Context,
        okHttpClient: OkHttpClient,
        moshi: Moshi,
    ): Retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl(context.getString(R.string.api_google_server))
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    @Provides
    @Singleton
    fun provideSNUTTRestApiForGoogle(retrofit: Retrofit): SNUTTRestApiForGoogle = retrofit.create(SNUTTRestApiForGoogle::class.java)

    private const val SIZE_OF_CACHE = (
        10 * 1024 * 1024 // 10 MB
        ).toLong()
}
