package com.wafflestudio.snutt_staging;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.facebook.FacebookSdk;
import com.wafflestudio.snutt_staging.manager.LectureManager;
import com.wafflestudio.snutt_staging.manager.NotiManager;
import com.wafflestudio.snutt_staging.manager.PrefManager;
import com.wafflestudio.snutt_staging.manager.TableManager;
import com.wafflestudio.snutt_staging.manager.TagManager;
import com.wafflestudio.snutt_staging.manager.UserManager;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

/**
 * Created by makesource on 2016. 1. 17..
 */
public class SNUTTApplication extends Application {

    private static Context context;

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
        restUrl = getString(R.string.api_server);
        super.onCreate();
    }

    //dp to px
    public static float dpTopx(float dp){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi/160f);
        return px;
    }
    //px to dp
    public static float pxTodp(float px){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }
    //sp to px
    public static float spTopx(float sp) {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return sp*scaledDensity;
    }

    //px to sp
    public static float pxTosp(float px) {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return px/scaledDensity;
    }

    public SNUTTRestApi getRestService() {
        if (restService == null) {
            RequestInterceptor requestInterceptor = new RequestInterceptor() {
                @Override
                public void intercept(RequestInterceptor.RequestFacade request) {
                    request.addHeader("x-access-apikey", getResources().getString(R.string.api_key));
                }
            };
            restAdapter = new RestAdapter.Builder()
                    .setEndpoint(restUrl)
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .setRequestInterceptor(requestInterceptor)
                    .build();
            restService = restAdapter.create(SNUTTRestApi.class);
        }
        return restService;
    }
}
