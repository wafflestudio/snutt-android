package com.wafflestudio.snutt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import com.wafflestudio.snutt.ui.TableListActivity;

/**
 * Created by makesource on 2016. 1. 16..
 */
public class SNUTTBaseActivity extends AppCompatActivity {

    public void startTableList() {
        Intent intent = new Intent(this, TableListActivity.class);
        startActivity(intent);
    }

    public SNUTTApplication getApp() {
        return (SNUTTApplication) getApplication();
    }
}
