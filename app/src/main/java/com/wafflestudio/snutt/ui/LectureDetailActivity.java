package com.wafflestudio.snutt.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.wafflestudio.snutt.R;
import com.wafflestudio.snutt.SNUTTBaseActivity;
import com.wafflestudio.snutt.manager.LectureManager;
import com.wafflestudio.snutt.model.ClassTime;
import com.wafflestudio.snutt.model.Lecture;
import com.wafflestudio.snutt.ui.adapter.ClassTimeAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by makesource on 2016. 3. 1..
 */
public class LectureDetailActivity extends SNUTTBaseActivity {

    private static final String TAG = "LECTURE_DETAIL_ACTIVITY" ;

    private List<Lecture> myLectures;
    private List<ClassTime> classTimes;
    private ClassTimeAdapter adapter;

    private ListView timeListView;
    private TextView classification;
    private TextView department;
    private TextView academic_year;
    private TextView course_number;
    private TextView lecture_number;
    private EditText course_title;
    private TextView credit;
    private TextView class_time;
    private EditText instructor;
    private TextView remark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture_detail);
        timeListView = (ListView) findViewById(R.id.time_listView);
        classification = (TextView) findViewById(R.id.classification);
        department = (TextView) findViewById(R.id.department);
        academic_year = (TextView) findViewById(R.id.academic_year);
        course_number = (TextView) findViewById(R.id.course_number);
        lecture_number = (TextView) findViewById(R.id.lecture_number);
        course_title = (EditText) findViewById(R.id.course_title);
        credit = (TextView) findViewById(R.id.credit);
        class_time = (TextView) findViewById(R.id.class_time);
        instructor = (EditText) findViewById(R.id.instructor);
        remark = (TextView) findViewById(R.id.remark);

        myLectures = LectureManager.getInstance().getLectures();

        Intent intent = getIntent();
        int position = intent.getIntExtra(INTENT_KEY_LECTURE_POSITION, -1);

        // throws exception when the list position is out of range
        Preconditions.checkPositionIndex(position, myLectures.size());

        Lecture lecture = myLectures.get(position);
        getSupportActionBar().setTitle("강의 상세보기");

        classification.setText(lecture.getClassification());
        department.setText(lecture.getDepartment());
        academic_year.setText(lecture.getAcademic_year());
        course_number.setText(lecture.getCourse_number());
        lecture_number.setText(lecture.getLecture_number());
        course_title.setHint(lecture.getCourse_title());
        course_title.setText(lecture.getCourse_title());
        credit.setText(String.valueOf(lecture.getCredit()));
        class_time.setText(lecture.getClass_time());
        instructor.setHint(lecture.getInstructor());
        instructor.setText(lecture.getInstructor());
        remark.setText(lecture.getRemark());

        classTimes = new ArrayList<>();
        for (JsonElement element : lecture.getClass_time_json()) {
            JsonObject jsonObject = element.getAsJsonObject();
            ClassTime classTime = new ClassTime(jsonObject);
            classTimes.add(classTime);
        }

        adapter = new ClassTimeAdapter(this, classTimes);
        timeListView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lecture_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        if (id == R.id.action_confirm) {
            // 강의 상세정보 수정 요청 (server)
            finish();
            return true;
        }  else if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
