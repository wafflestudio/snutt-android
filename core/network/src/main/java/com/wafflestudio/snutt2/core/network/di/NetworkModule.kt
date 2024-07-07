package com.wafflestudio.snutt2.core.network.di

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.provider.Settings.Secure
import com.squareup.moshi.Moshi
import com.wafflestudio.snutt2.core.database.preference.SNUTTStorageTemp
import com.wafflestudio.snutt2.core.network.BuildConfig
import com.wafflestudio.snutt2.core.network.NetworkLog
import com.wafflestudio.snutt2.core.network.R
import com.wafflestudio.snutt2.core.network.createNewNetworkLog
import com.wafflestudio.snutt2.core.network.retrofit.RetrofitSNUTTNetworkApi
import com.wafflestudio.snutt2.core.network.toDatabaseModel
import com.wafflestudio.snutt2.core.network.util.ErrorParsingCallAdapterFactory
import com.wafflestudio.snutt2.core.network.util.Serializer
import com.wafflestudio.snutt2.core.qualifiers.CoreDatabase
import com.wafflestudio.snutt2.core.qualifiers.CoreNetwork
//import com.wafflestudio.snutt2.data.SNUTTStorage
//import com.wafflestudio.snutt2.data.addNetworkLog
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
import javax.inject.Singleton

// TODO : 아직은 이 NetworkModule이 아니라 원래 있던 NetworkModule을 쓰고 있다. (순환 종속 방지)

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @SuppressLint("HardwareIds")
    @CoreNetwork
    @Provides
    @Singleton
    fun provideOkHttpClient(
        @ApplicationContext context: Context,
        @CoreDatabase snuttStorage: SNUTTStorageTemp,
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
                        context.packageManager.getPackageInfo(context.packageName, 0).versionName,
                    )
                    .addHeader(
                        "x-app-type",
                        if (BuildConfig.DEBUG) "debug" else "release",
                    )
                    .addHeader(
                        "x-device-id",
                        Secure.getString(
                            context.contentResolver,
                            Secure.ANDROID_ID,
                        ),
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
                if (BuildConfig.DEBUG) snuttStorage.addNetworkLog(chain.createNewNetworkLog(context, response).toDatabaseModel()) // TODO : addNetworkLog 옮기면서 type를 바꿔줘야 할 듯
                response
            }
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = if (BuildConfig.DEBUG) {
                        okhttp3.logging.HttpLoggingInterceptor.Level.BODY
                    } else {
                        okhttp3.logging.HttpLoggingInterceptor.Level.NONE
                    }
                },
            )
            .build()
    }

    @Provides
    @CoreNetwork
    fun provideRetrofit(
        @ApplicationContext context: Context,
        @CoreNetwork okHttpClient: OkHttpClient,
        @CoreNetwork moshi: Moshi,
        @CoreNetwork serializer: Serializer,
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
    @CoreNetwork
    @Singleton
    fun provideSNUTTRestApi(@CoreNetwork retrofit: Retrofit): RetrofitSNUTTNetworkApi {
        return retrofit.create(RetrofitSNUTTNetworkApi::class.java)
    }

    private const val SIZE_OF_CACHE = (
        10 * 1024 * 1024 // 10 MB
        ).toLong()

    @Provides
    @CoreNetwork
    @Singleton
    fun provideConnectivityManager(
        @ApplicationContext context: Context,
    ): ConnectivityManager {
        return (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
    }
}
