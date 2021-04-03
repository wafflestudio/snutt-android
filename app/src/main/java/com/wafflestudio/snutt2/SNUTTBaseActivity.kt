package com.wafflestudio.snutt2

import android.app.Activity
import android.content.Intent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.wafflestudio.snutt2.ui.*
import java.util.*

/**
 * Created by makesource on 2016. 1. 16..
 */
open class SNUTTBaseActivity : AppCompatActivity() {
    fun startMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    fun startTableList() {
        val intent = Intent(this, TableListActivity::class.java)
        startActivity(intent)
    }

    fun startTableView(id: String?) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(INTENT_KEY_TABLE_ID, id)
        startActivity(intent)
    }

    // This method is for create custom lecture
    fun startLectureMain() {
        val intent = Intent(this, LectureMainActivity::class.java)
        startActivity(intent)
    }

    fun startLectureMain(position: Int) {
        val intent = Intent(this, LectureMainActivity::class.java)
        intent.putExtra(INTENT_KEY_LECTURE_POSITION, position)
        startActivity(intent)
    }

    fun startTableCreate() {
        val intent = Intent(this, TableCreateActivity::class.java)
        startActivity(intent)
    }

    fun startWelcome(type: Int) {
        val intent = Intent(this, WelcomeActivity::class.java)
        intent.putExtra(INTENT_KEY_FRAGMENT_TYPE, type)
        startActivity(intent)
    }

    fun startSettingsMain(type: Int) {
        val intent = Intent(this, SettingsMainActivity::class.java)
        intent.putExtra(INTENT_KEY_SETTINGS_TYPE, type)
        startActivity(intent)
    }

    fun startIntro() {
        val intent = Intent(this, IntroActivity::class.java)
        startActivity(intent)
    }

    fun finishAll() {
        for (activity in activityList) {
            activity.finish()
        }
    }

    val app: SNUTTApplication
        get() = application as SNUTTApplication

    fun hideSoftKeyboard(view: View) {
        val mgr = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        mgr.hideSoftInputFromWindow(view.windowToken, 0)
    }

    companion object {
        const val INTENT_KEY_FRAGMENT_TYPE = "INTENT_KEY_FRAGMENT_TYPE"
        const val INTENT_KEY_TABLE_ID = "INTENT_KEY_TABLE_ID"
        const val INTENT_KEY_LECTURE_POSITION = "INTENT_KEY_LECTURE_POSITION"
        const val INTENT_KEY_SETTINGS_TYPE = "INTENT_KEY_SETTINGS_TYPE"
        @JvmField
        var activityList = ArrayList<Activity>()
    }
}