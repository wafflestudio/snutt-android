package com.wafflestudio.snutt2

import android.app.Application
import android.content.Context
import com.facebook.FacebookSdk
import com.wafflestudio.snutt2.manager.*
import com.wafflestudio.snutt2.network.SNUTTRestApi
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File

/**
 * Created by makesource on 2016. 1. 17..
 */
class SNUTTApplication : Application() {
    val restService: SNUTTRestApi? by lazy {
//        val requestInterceptor = RequestInterceptor { request ->
//            request.addHeader(
//                "x-access-apikey",
//                resources.getString(R.string.api_key)
//            )
//        }
//        val okHttpClient = OkHttpClient()
//        okHttpClient.cache = cache
//        restAdapter = RestAdapter.Builder()
//            .setEndpoint(restUrl)
//            .setLogLevel(RestAdapter.LogLevel.FULL)
//            .setRequestInterceptor(requestInterceptor)
//            .setClient(OkClient(okHttpClient))
//            .setErrorHandler(RetrofitErrorHandler(applicationContext))
//            .build()
        val cache = Cache(File(context!!.cacheDir, "http"), SIZE_OF_CACHE)
        val okHttpClient = OkHttpClient.Builder()
            .cache(cache)
            .build()

        val retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(restUrl!!)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

        retrofit.create(SNUTTRestApi::class.java)
    }

    private var restUrl: String? = null
    override fun onCreate() {
        context = applicationContext
        PrefManager.getInstance(context!!)
        FacebookSdk.sdkInitialize(context)
        LectureManager.instance
        TagManager.instance
        UserManager.instance
        TableManager.instance
        NotiManager.getInstance(this)
        SNUTTUtils.context = context
        restUrl = getString(R.string.api_server)
        super.onCreate()
    }

    companion object {
        private const val TAG = "SNUTT_APPLICATION"
        private var context: Context? = null
        private const val SIZE_OF_CACHE = (
            10 * 1024 * 1024 // 10 MB
            ).toLong()
    }
}
