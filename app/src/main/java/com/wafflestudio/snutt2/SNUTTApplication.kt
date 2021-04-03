package com.wafflestudio.snutt2

import android.app.Application
import android.content.Context
import com.facebook.FacebookSdk
import com.squareup.okhttp.Cache
import com.squareup.okhttp.OkHttpClient
import com.wafflestudio.snutt2.handler.RetrofitErrorHandler
import com.wafflestudio.snutt2.manager.*
import retrofit.RequestInterceptor
import retrofit.RestAdapter
import retrofit.client.OkClient
import java.io.File

/**
 * Created by makesource on 2016. 1. 17..
 */
class SNUTTApplication : Application() {
    private var restAdapter: RestAdapter? = null
    val restService: SNUTTRestApi? by lazy {
        val requestInterceptor = RequestInterceptor { request -> request.addHeader("x-access-apikey", resources.getString(R.string.api_key)) }
        val cache = Cache(File(context!!.cacheDir, "http"), SIZE_OF_CACHE)
        val okHttpClient = OkHttpClient()
        okHttpClient.cache = cache
        restAdapter = RestAdapter.Builder()
                .setEndpoint(restUrl)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setRequestInterceptor(requestInterceptor)
                .setClient(OkClient(okHttpClient))
                .setErrorHandler(RetrofitErrorHandler(applicationContext))
                .build()
        restAdapter?.create(SNUTTRestApi::class.java)
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
        private const val SIZE_OF_CACHE = (10 * 1024 * 1024 // 10 MB
                ).toLong()
    }
}