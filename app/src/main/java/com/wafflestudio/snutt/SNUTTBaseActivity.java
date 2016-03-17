package com.wafflestudio.snutt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import com.wafflestudio.snutt.ui.LectureDetailActivity;
import com.wafflestudio.snutt.ui.MainActivity;
import com.wafflestudio.snutt.ui.TableCreateActivity;
import com.wafflestudio.snutt.ui.TableListActivity;
import com.wafflestudio.snutt.ui.WelcomeActivity;

/**
 * Created by makesource on 2016. 1. 16..
 */
public class SNUTTBaseActivity extends AppCompatActivity {

    public static final String INTENT_KEY_TABLE_ID = "INTENT_KEY_TABLE_ID";
    public static final String INTENT_KEY_LECTURE_POSITION = "INTENT_KEY_LECTURE_POSITION";

    public void startTableList() {
        Intent intent = new Intent(this, TableListActivity.class);
        startActivity(intent);
    }

    public void startTableView(String id) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(INTENT_KEY_TABLE_ID, id);
        startActivity(intent);
    }

    public void startLectureDetail(int position) {
        Intent intent = new Intent(this, LectureDetailActivity.class);
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

    public SNUTTApplication getApp() {
        return (SNUTTApplication) getApplication();
    }
}
