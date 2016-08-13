package com.wafflestudio.snutt;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.wafflestudio.snutt.manager.LectureManager;
import com.wafflestudio.snutt.manager.PrefManager;
import com.wafflestudio.snutt.manager.TableManager;
import com.wafflestudio.snutt.manager.TagManager;
import com.wafflestudio.snutt.manager.UserManager;
import com.wafflestudio.snutt.model.Lecture;

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
        LectureManager.getInstance(context);
        TagManager.getInstance(this);
        UserManager.getInstance(this);
        TableManager.getInstance(this);
        restUrl = "http://walnut.wafflestudio.com:3000/api";

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
