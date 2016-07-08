package com.wafflestudio.snutt.ui;

import android.os.Bundle;

import com.wafflestudio.snutt.R;
import com.wafflestudio.snutt.SNUTTBaseActivity;
import com.wafflestudio.snutt.manager.LectureManager;
import com.wafflestudio.snutt.view.TableView;

/**
 * Created by makesource on 2016. 5. 27..
 */
public class LectureCreateActivity extends SNUTTBaseActivity implements LectureManager.OnLectureChangedListener {

    private static TableView mInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture_create);
        setTitle("커스텀 강의 추가");

        mInstance = (TableView) findViewById(R.id.timetable);

    }

    @Override
    public void onPause() {
        super.onPause();
        LectureManager.getInstance().removeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        LectureManager.getInstance().addListener(this);
    }

    @Override
    public void notifyLectureChanged() {
        mInstance.invalidate();
    }
}
