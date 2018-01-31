package com.wafflestudio.snutt2.manager;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.wafflestudio.snutt2.model.Table;
import com.wafflestudio.snutt2.provider.TimetableWidgetProvider;

/**
 * Created by makesource on 2016. 1. 24..
 */
public class PrefManager {
    private static final String TAG = "PrefManager" ;

    private static PrefManager singletonInstance;

    private Context context;
    private SharedPreferences sp;

    private static final String PREF_KEY_LAST_VIEW_TABLE_ID = "pref_key_last_view_table_id";
    private static final String PREF_KEY_X_ACCESS_TOKEN = "pref_key_x_access_token";
    private static final String PREF_KEY_USER_ID = "pref_key_user_id";
    private static final String PREF_KEY_CURRENT_YEAR = "pref_key_current_year" ;
    private static final String PREF_KEY_CURRENT_SEMESTER = "pref_key_current_semester" ;
    private static final String PREF_KEY_CURRENT_TABLE = "pref_key_current_table" ;
    private static final String PREF_KEY_TRIM_WIDTH_START = "pref_key_trim_width_start";
    private static final String PREF_KEY_TRIM_WIDTH_NUM = "pref_key_trim_width_num";
    private static final String PREF_KEY_TRIM_HEIGHT_START = "pref_key_trim_height_start";
    private static final String PREF_KEY_TRIM_HEIGHT_NUM = "pref_key_trim_height_num";
    private static final String PREF_KEY_AUTO_TRIM = "pref_key_auto_trim";
    private static final String PREF_KEY_LECTURE_COLORS = "pref_key_lecture_colors";
    private static final String PREF_KEY_LECTURE_COLOR_NAMES = "pref_key_lecture_color_names";
    private static final String PREF_KEY_HIDDEN_CAPTURE_BUTTON = "pref_key_hidden_capture_button";

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

    public void resetPrefValue() {
        sp.edit().clear().commit();
        sendWidgetUpdateIntent();
    }

    public void updateNewTable(Table table) {
        String json = new Gson().toJson(table);
        setLastViewTableId(table.getId());
        setCurrentTable(json);
        setCurrentYear(table.getYear());
        setCurrentSemester(table.getSemester());
        sendWidgetUpdateIntent();
        Log.d(TAG, "update new table : " + json);
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
        return sp.getString(PREF_KEY_X_ACCESS_TOKEN, null);
    }

    public void setPrefKeyUserId(String user_id) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(PREF_KEY_USER_ID, user_id);
        editor.apply();
    }

    public String getPrefKeyUserId() {
        return sp.getString(PREF_KEY_USER_ID, null);
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

    public void setCurrentTable(String table) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(PREF_KEY_CURRENT_TABLE, table);
        editor.apply();
        sendWidgetUpdateIntent();
    }

    public String getCurrentTable() {
        return sp.getString(PREF_KEY_CURRENT_TABLE, null);
    }

    public void setTrimWidthStart(int start) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(PREF_KEY_TRIM_WIDTH_START, start);
        editor.apply();
    }

    public int getTrimWidthStart() {
        return sp.getInt(PREF_KEY_TRIM_WIDTH_START, 0);
    }

    public void setTrimWidthNum(int num) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(PREF_KEY_TRIM_WIDTH_NUM, num);
        editor.apply();
    }

    public int getTrimWidthNum() {
        return sp.getInt(PREF_KEY_TRIM_WIDTH_NUM, 7);
    }

    public void setTrimHeightStart(int start) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(PREF_KEY_TRIM_HEIGHT_START, start);
        editor.apply();
    }

    public int getTrimHeightStart() {
        return sp.getInt(PREF_KEY_TRIM_HEIGHT_START, 0);
    }

    public void setTrimHeightNum(int num) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(PREF_KEY_TRIM_HEIGHT_NUM, num);
        editor.apply();
    }

    public int getTrimHeightNum() {
        return sp.getInt(PREF_KEY_TRIM_HEIGHT_NUM, 14);
    }

    public void setAutoTrim(boolean autoTrim) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(PREF_KEY_AUTO_TRIM, autoTrim);
        editor.apply();
    }

    public boolean getAutoTrim() {
        return sp.getBoolean(PREF_KEY_AUTO_TRIM, true);
    }

    public void setLectureColors(String colors) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(PREF_KEY_LECTURE_COLORS, colors);
        editor.apply();
    }

    public String getLectureColors() {
        return sp.getString(PREF_KEY_LECTURE_COLORS, null);
    }

    public void setLectureColorNames(String names) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(PREF_KEY_LECTURE_COLOR_NAMES, names);
        editor.apply();
    }

    public String getLectureColorNames() {
        return sp.getString(PREF_KEY_LECTURE_COLOR_NAMES, null);
    }

    public void setHiddenCaptureButton(boolean hidden) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(PREF_KEY_HIDDEN_CAPTURE_BUTTON, hidden);
        editor.apply();
    }

    public boolean getHiddenCaptureButton() {
        return sp.getBoolean(PREF_KEY_HIDDEN_CAPTURE_BUTTON, false);
    }

    private void sendWidgetUpdateIntent() {
        AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
        ComponentName widgetComponent = new ComponentName(context, TimetableWidgetProvider.class);
        int[] widgetIds = widgetManager.getAppWidgetIds(widgetComponent);
        Intent update = new Intent();
        update.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds);
        update.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        context.sendBroadcast(update);
    }
}
