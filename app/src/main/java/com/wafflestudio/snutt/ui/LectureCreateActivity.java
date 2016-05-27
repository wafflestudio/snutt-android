package com.wafflestudio.snutt.ui;

import android.os.Bundle;

import com.wafflestudio.snutt.R;
import com.wafflestudio.snutt.SNUTTBaseActivity;

/**
 * Created by makesource on 2016. 5. 27..
 */
public class LectureCreateActivity extends SNUTTBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture_create);
        setTitle("커스텀 강의 추가");


    }
}
