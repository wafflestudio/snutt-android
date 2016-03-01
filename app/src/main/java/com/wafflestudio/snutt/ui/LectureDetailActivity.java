package com.wafflestudio.snutt.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.common.base.Preconditions;
import com.wafflestudio.snutt.R;
import com.wafflestudio.snutt.SNUTTBaseActivity;
import com.wafflestudio.snutt.manager.LectureManager;
import com.wafflestudio.snutt.model.Lecture;

import java.util.List;

/**
 * Created by makesource on 2016. 3. 1..
 */
public class LectureDetailActivity extends SNUTTBaseActivity {

    private static final String TAG = "LECTURE_DETAIL_ACTIVITY" ;

    private List<Lecture> myLectures;

    private TextView classification;
    private TextView department;
    private TextView academic_year;
    private TextView course_number;
    private TextView lecture_number;
    private TextView course_title;
    private TextView credit;
    private TextView class_time;
    private TextView instructor;
    private TextView remark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture_detail);
        classification = (TextView) findViewById(R.id.classification);
        department = (TextView) findViewById(R.id.department);
        academic_year = (TextView) findViewById(R.id.academic_year);
        course_number = (TextView) findViewById(R.id.course_number);
        lecture_number = (TextView) findViewById(R.id.lecture_number);
        course_title = (TextView) findViewById(R.id.course_title);
        credit = (TextView) findViewById(R.id.credit);
        class_time = (TextView) findViewById(R.id.class_time);
        instructor = (TextView) findViewById(R.id.instructor);
        remark = (TextView) findViewById(R.id.remark);

        myLectures = LectureManager.getInstance().getLectures();

        Intent intent = getIntent();
        int position = intent.getIntExtra(INTENT_KEY_LECTURE_POSITION, -1);

        // throws exception when the list position is out of range
        Preconditions.checkPositionIndex(position, myLectures.size());

        Lecture lecture = myLectures.get(position);

        classification.setText(lecture.getClassification());
        department.setText(lecture.getDepartment());
        academic_year.setText(lecture.getAcademic_year());
        course_number.setText(lecture.getCourse_title());
        lecture_number.setText(lecture.getLecture_number());
        course_title.setText(lecture.getCourse_title());
        credit.setText(String.valueOf(lecture.getCredit()));
        class_time.setText(lecture.getClass_time());
        instructor.setText(lecture.getInstructor());
        remark.setText(lecture.getRemark());
    }
}
