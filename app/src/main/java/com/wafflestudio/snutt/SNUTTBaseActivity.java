package com.wafflestudio.snutt;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import com.wafflestudio.snutt.ui.LectureCreateActivity;
import com.wafflestudio.snutt.ui.LectureMainActivity;
import com.wafflestudio.snutt.ui.MainActivity;
import com.wafflestudio.snutt.ui.SettingsMainActivity;
import com.wafflestudio.snutt.ui.TableCreateActivity;
import com.wafflestudio.snutt.ui.TableListActivity;
import com.wafflestudio.snutt.ui.WelcomeActivity;

import java.util.ArrayList;

/**
 * Created by makesource on 2016. 1. 16..
 */
public class SNUTTBaseActivity extends AppCompatActivity {

    public static final String INTENT_KEY_TABLE_ID = "INTENT_KEY_TABLE_ID";
    public static final String INTENT_KEY_LECTURE_POSITION = "INTENT_KEY_LECTURE_POSITION";
    public static final String INTENT_KEY_SETTINGS_TYPE = "INTENT_KEY_SETTINGS_TYPE";
    public static ArrayList<Activity> activityList = new ArrayList<Activity>();

    public void startMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void startTableList() {
        Intent intent = new Intent(this, TableListActivity.class);
        startActivity(intent);
    }

    public void startTableView(String id) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(INTENT_KEY_TABLE_ID, id);
        startActivity(intent);
    }

    // This method is for create custom lecture
    public void startLectureMain() {
        Intent intent = new Intent(this, LectureMainActivity.class);
        startActivity(intent);
    }

    public void startLectureMain(int position) {
        Intent intent = new Intent(this, LectureMainActivity.class);
        intent.putExtra(INTENT_KEY_LECTURE_POSITION, position);
        startActivity(intent);
    }

    public void startTableCreate() {
        Intent intent = new Intent(this, TableCreateActivity.class);
        startActivity(intent);
    }

    public void startWelcome() {
        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
    }

    public void startLectureCreate() {
        Intent intent = new Intent(this, LectureCreateActivity.class);
        startActivity(intent);
    }

    public void startSettingsMain(int type) {
        Intent intent = new Intent(this, SettingsMainActivity.class);
        intent.putExtra(INTENT_KEY_SETTINGS_TYPE, type);
        startActivity(intent);
    }

    public void finishAll() {
        for (Activity activity : activityList) {
            activity.finish();
        }
    }

    public SNUTTApplication getApp() {
        return (SNUTTApplication) getApplication();
    }
}
