package com.wafflestudio.snutt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import com.wafflestudio.snutt.ui.MainActivity;
import com.wafflestudio.snutt.ui.TableListActivity;

/**
 * Created by makesource on 2016. 1. 16..
 */
public class SNUTTBaseActivity extends AppCompatActivity {

    public static final String INTENT_KEY_TABLE_ID = "intent_key_table_key";

    public void startTableList() {
        Intent intent = new Intent(this, TableListActivity.class);
        startActivity(intent);
    }

    public void startTableView(String id) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(INTENT_KEY_TABLE_ID, id);
        startActivity(intent);
    }

    public SNUTTApplication getApp() {
        return (SNUTTApplication) getApplication();
    }
}
