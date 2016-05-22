package com.wafflestudio.snutt.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.common.base.Preconditions;

/**
 * Created by makesource on 2016. 1. 24..
 */
public class PrefManager {
    private static final String TAG = "PrefManager" ;

    private static PrefManager singletonInstance;

    private Context context;
    private SharedPreferences sp;

    private static final String PREF_KEY_LAST_VIEW_TABLE_ID = "pref_key_last_view_table_id" ;
    private static final String PREF_KEY_X_ACCESS_TOKEN = "pref_key_x_access_token" ;
    private static final String PREF_KEY_CURRENT_YEAR = "pref_key_current_year" ;
    private static final String PREF_KEY_CURRENT_SEMESTER = "pref_key_current_semester" ;
    private String defToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyIkX18iOnsic3RyaWN0TW9kZSI6dHJ1ZSwiZ2V0dGVycyI6eyJsb2NhbCI6eyJwYXNzd29yZCI6IiQyYSQwNCRZNVZjczd1WERLemIuWkdXOVFjZkV1TFRucUhhd1VRLm52VXk2Wk9WRjM3TUpRaXNPcE8vUyIsImlkIjoic251dHQifX0sIndhc1BvcHVsYXRlZCI6ZmFsc2UsInNjb3BlIjp7Il9pZCI6IjU2OTVlYTg5YzNlMjU0ODYwOTcwZWY2YyIsIl9fdiI6MCwicmVnRGF0ZSI6IjIwMTYtMDEtMTNUMDY6MTE6MDcuMjM1WiIsImlzQWRtaW4iOmZhbHNlLCJsb2NhbCI6eyJpZCI6InNudXR0IiwicGFzc3dvcmQiOiIkMmEkMDQkWTVWY3M3dVhES3piLlpHVzlRY2ZFdUxUbnFIYXdVUS5udlV5NlpPVkYzN01KUWlzT3BPL1MifX0sImFjdGl2ZVBhdGhzIjp7InBhdGhzIjp7ImlzQWRtaW4iOiJpbml0IiwicmVnRGF0ZSI6ImluaXQiLCJfX3YiOiJpbml0IiwibG9jYWwucGFzc3dvcmQiOiJpbml0IiwibG9jYWwuaWQiOiJpbml0IiwiX2lkIjoiaW5pdCJ9LCJzdGF0ZXMiOnsiaWdub3JlIjp7fSwiZGVmYXVsdCI6e30sImluaXQiOnsiX192Ijp0cnVlLCJsb2NhbC5wYXNzd29yZCI6dHJ1ZSwibG9jYWwuaWQiOnRydWUsImlzQWRtaW4iOnRydWUsInJlZ0RhdGUiOnRydWUsIl9pZCI6dHJ1ZX0sIm1vZGlmeSI6e30sInJlcXVpcmUiOnt9fSwic3RhdGVOYW1lcyI6WyJyZXF1aXJlIiwibW9kaWZ5IiwiaW5pdCIsImRlZmF1bHQiLCJpZ25vcmUiXX0sImVtaXR0ZXIiOnsiZG9tYWluIjpudWxsLCJfZXZlbnRzIjp7fSwiX2V2ZW50c0NvdW50IjowLCJfbWF4TGlzdGVuZXJzIjowfX0sImlzTmV3IjpmYWxzZSwiX2RvYyI6eyJsb2NhbCI6eyJwYXNzd29yZCI6IiQyYSQwNCRZNVZjczd1WERLemIuWkdXOVFjZkV1TFRucUhhd1VRLm52VXk2Wk9WRjM3TUpRaXNPcE8vUyIsImlkIjoic251dHQifSwiZmFjZWJvb2siOnt9LCJpc0FkbWluIjpmYWxzZSwicmVnRGF0ZSI6IjIwMTYtMDEtMTNUMDY6MTE6MDcuMjM1WiIsIl9fdiI6MCwiX2lkIjoiNTY5NWVhODljM2UyNTQ4NjA5NzBlZjZjIn0sIl9wcmVzIjp7IiRfX29yaWdpbmFsX3NhdmUiOltudWxsLG51bGwsbnVsbF19LCJfcG9zdHMiOnsiJF9fb3JpZ2luYWxfc2F2ZSI6W119LCJpYXQiOjE0NTMyOTU2NzcsImV4cCI6MTQ2ODg0NzY3N30.Pb4OgIxZIKTwoCOwhJiAX0Tv6L2lJa7Ivkn5-QGl5EA" ;


    private PrefManager(Context context) {
        Preconditions.checkNotNull(context);
        this.context = context;
        this.sp = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static PrefManager getInstance(Context context) {
        singletonInstance = new PrefManager(context);
        return singletonInstance;
    }

    public static PrefManager getInstance() {
        return singletonInstance;
    }

    public void setLastViewTableId(String id) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(PREF_KEY_LAST_VIEW_TABLE_ID, id);
        editor.apply();
    }
    public String getLastViewTableId() {
        return sp.getString(PREF_KEY_LAST_VIEW_TABLE_ID, null);
    }

    public void setPrefKeyXAccessToken(String token) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(PREF_KEY_X_ACCESS_TOKEN, token);
        editor.apply();
    }

    public String getPrefKeyXAccessToken() {
        return sp.getString(PREF_KEY_X_ACCESS_TOKEN, defToken);
    }

    public void setCurrentYear(int year) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(PREF_KEY_CURRENT_YEAR, year);
        editor.apply();
    }

    public int getCurrentYear() {
        return sp.getInt(PREF_KEY_CURRENT_YEAR, 0);
    }

    public void setCurrentSemester(int semester) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(PREF_KEY_CURRENT_SEMESTER, semester);
        editor.apply();
    }

    public int getCurrentSemester() {
        return sp.getInt(PREF_KEY_CURRENT_SEMESTER, 0);
    }




}
