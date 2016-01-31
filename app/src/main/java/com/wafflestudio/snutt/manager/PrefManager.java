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

    public static final String PREF_KEY_LAST_VIEW_TABLE_ID = "pref_key_last_view_table_id" ;

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

}
