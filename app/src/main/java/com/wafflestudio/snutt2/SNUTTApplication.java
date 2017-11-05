package com.wafflestudio.snutt2;

import android.app.Application;
import android.content.Context;

import com.facebook.FacebookSdk;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.wafflestudio.snutt2.handler.RetrofitErrorHandler;
import com.wafflestudio.snutt2.manager.LectureManager;
import com.wafflestudio.snutt2.manager.NotiManager;
import com.wafflestudio.snutt2.manager.PrefManager;
import com.wafflestudio.snutt2.manager.TableManager;
import com.wafflestudio.snutt2.manager.TagManager;
import com.wafflestudio.snutt2.manager.UserManager;

import java.io.File;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * Created by makesource on 2016. 1. 17..
 */
public class SNUTTApplication extends Application {

    private static final String TAG = "SNUTT_APPLICATION";
    private static Context context;
    private static long SIZE_OF_CACHE = 10 * 1024 * 1024; // 10 MB

    private RestAdapter restAdapter;
    private SNUTTRestApi restService;
    private String restUrl;

    @Override
    public void onCreate() {
        context = getApplicationContext();
        PrefManager.getInstance(context);
        FacebookSdk.sdkInitialize(context);
        LectureManager.getInstance(this);
        TagManager.getInstance(this);
        UserManager.getInstance(this);
        TableManager.getInstance(this);
        NotiManager.getInstance(this);
        SNUTTUtils.context = context;
        restUrl = getString(R.string.api_server);
        super.onCreate();
    }

    public SNUTTRestApi getRestService() {
        if (restService == null) {
            RequestInterceptor requestInterceptor = new RequestInterceptor() {
                @Override
                public void intercept(RequestInterceptor.RequestFacade request) {
                    request.addHeader("x-access-apikey", getResources().getString(R.string.api_key));
                }
            };
            Cache cache = new Cache(new File(context.getCacheDir(), "http"), SIZE_OF_CACHE);
            OkHttpClient okHttpClient = new OkHttpClient();
            okHttpClient.setCache(cache);

            restAdapter = new RestAdapter.Builder()
                    .setEndpoint(restUrl)
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .setRequestInterceptor(requestInterceptor)
                    .setClient(new OkClient(okHttpClient))
                    .setErrorHandler(new RetrofitErrorHandler(getApplicationContext()))
                    .build();

            restService = restAdapter.create(SNUTTRestApi.class);
        }
        return restService;
    }
}
