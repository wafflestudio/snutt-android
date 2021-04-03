package com.wafflestudio.snutt2.manager

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import com.google.common.base.Preconditions
import com.google.gson.Gson
import com.wafflestudio.snutt2.model.Table
import com.wafflestudio.snutt2.provider.TimetableWidgetProvider

/**
 * Created by makesource on 2016. 1. 24..
 */
class PrefManager private constructor(context: Context) {
    private val context: Context
    private val sp: SharedPreferences
    fun resetPrefValue() {
        sp.edit().clear().commit()
        sendWidgetUpdateIntent()
    }

    fun updateNewTable(table: Table) {
        val json = Gson().toJson(table)
        lastViewTableId = table.id
        currentTable = json
        currentYear = table.year
        currentSemester = table.semester
        sendWidgetUpdateIntent()
        Log.d(TAG, "update new table : $json")
    }

    var lastViewTableId: String?
        get() = sp.getString(PREF_KEY_LAST_VIEW_TABLE_ID, null)
        set(id) {
            val editor = sp.edit()
            editor.putString(PREF_KEY_LAST_VIEW_TABLE_ID, id)
            editor.apply()
        }
    var prefKeyXAccessToken: String?
        get() = sp.getString(PREF_KEY_X_ACCESS_TOKEN, null)
        set(token) {
            val editor = sp.edit()
            editor.putString(PREF_KEY_X_ACCESS_TOKEN, token)
            editor.apply()
        }
    var prefKeyUserId: String?
        get() = sp.getString(PREF_KEY_USER_ID, null)
        set(user_id) {
            val editor = sp.edit()
            editor.putString(PREF_KEY_USER_ID, user_id)
            editor.apply()
        }
    var currentYear: Int
        get() = sp.getInt(PREF_KEY_CURRENT_YEAR, 0)
        set(year) {
            val editor = sp.edit()
            editor.putInt(PREF_KEY_CURRENT_YEAR, year)
            editor.apply()
        }
    var currentSemester: Int
        get() = sp.getInt(PREF_KEY_CURRENT_SEMESTER, 0)
        set(semester) {
            val editor = sp.edit()
            editor.putInt(PREF_KEY_CURRENT_SEMESTER, semester)
            editor.apply()
        }
    var currentTable: String?
        get() = sp.getString(PREF_KEY_CURRENT_TABLE, null)
        set(table) {
            val editor = sp.edit()
            editor.putString(PREF_KEY_CURRENT_TABLE, table)
            editor.apply()
            sendWidgetUpdateIntent()
        }
    var trimWidthStart: Int
        get() = sp.getInt(PREF_KEY_TRIM_WIDTH_START, 0)
        set(start) {
            val editor = sp.edit()
            editor.putInt(PREF_KEY_TRIM_WIDTH_START, start)
            editor.apply()
        }
    var trimWidthNum: Int
        get() = sp.getInt(PREF_KEY_TRIM_WIDTH_NUM, 7)
        set(num) {
            val editor = sp.edit()
            editor.putInt(PREF_KEY_TRIM_WIDTH_NUM, num)
            editor.apply()
        }
    var trimHeightStart: Int
        get() = sp.getInt(PREF_KEY_TRIM_HEIGHT_START, 0)
        set(start) {
            val editor = sp.edit()
            editor.putInt(PREF_KEY_TRIM_HEIGHT_START, start)
            editor.apply()
        }
    var trimHeightNum: Int
        get() = sp.getInt(PREF_KEY_TRIM_HEIGHT_NUM, 14)
        set(num) {
            val editor = sp.edit()
            editor.putInt(PREF_KEY_TRIM_HEIGHT_NUM, num)
            editor.apply()
        }
    var autoTrim: Boolean
        get() = sp.getBoolean(PREF_KEY_AUTO_TRIM, true)
        set(autoTrim) {
            val editor = sp.edit()
            editor.putBoolean(PREF_KEY_AUTO_TRIM, autoTrim)
            editor.apply()
        }
    var lectureColors: String?
        get() = sp.getString(PREF_KEY_LECTURE_COLORS, null)
        set(colors) {
            val editor = sp.edit()
            editor.putString(PREF_KEY_LECTURE_COLORS, colors)
            editor.apply()
        }
    var lectureColorNames: String?
        get() = sp.getString(PREF_KEY_LECTURE_COLOR_NAMES, null)
        set(names) {
            val editor = sp.edit()
            editor.putString(PREF_KEY_LECTURE_COLOR_NAMES, names)
            editor.apply()
        }

    private fun sendWidgetUpdateIntent() {
        val widgetManager = AppWidgetManager.getInstance(context)
        val widgetComponent = ComponentName(context, TimetableWidgetProvider::class.java)
        val widgetIds = widgetManager.getAppWidgetIds(widgetComponent)
        val update = Intent()
        update.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds)
        update.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        context.sendBroadcast(update)
    }

    companion object {
        private const val TAG = "PrefManager"
        var instance: PrefManager? = null
            private set
        private const val PREF_KEY_LAST_VIEW_TABLE_ID = "pref_key_last_view_table_id"
        private const val PREF_KEY_X_ACCESS_TOKEN = "pref_key_x_access_token"
        private const val PREF_KEY_USER_ID = "pref_key_user_id"
        private const val PREF_KEY_CURRENT_YEAR = "pref_key_current_year"
        private const val PREF_KEY_CURRENT_SEMESTER = "pref_key_current_semester"
        private const val PREF_KEY_CURRENT_TABLE = "pref_key_current_table"
        private const val PREF_KEY_TRIM_WIDTH_START = "pref_key_trim_width_start"
        private const val PREF_KEY_TRIM_WIDTH_NUM = "pref_key_trim_width_num"
        private const val PREF_KEY_TRIM_HEIGHT_START = "pref_key_trim_height_start"
        private const val PREF_KEY_TRIM_HEIGHT_NUM = "pref_key_trim_height_num"
        private const val PREF_KEY_AUTO_TRIM = "pref_key_auto_trim"
        private const val PREF_KEY_LECTURE_COLORS = "pref_key_lecture_colors"
        private const val PREF_KEY_LECTURE_COLOR_NAMES = "pref_key_lecture_color_names"
        fun getInstance(context: Context): PrefManager? {
            instance = PrefManager(context)
            return instance
        }
    }

    init {
        Preconditions.checkNotNull(context)
        this.context = context
        sp = PreferenceManager.getDefaultSharedPreferences(context)
    }
}