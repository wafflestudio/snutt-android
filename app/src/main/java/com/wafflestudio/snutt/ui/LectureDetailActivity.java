package com.wafflestudio.snutt.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.gson.JsonArray;
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
    private View bgColor;
    private View fgColor;

    private Lecture lecture;

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
        bgColor = (View) findViewById(R.id.bgColor);
        fgColor = (View) findViewById(R.id.fgColor);

        myLectures = LectureManager.getInstance().getLectures();

        Intent intent = getIntent();
        int position = intent.getIntExtra(INTENT_KEY_LECTURE_POSITION, -1);

        // throws exception when the list position is out of range
        Preconditions.checkPositionIndex(position, myLectures.size());

        lecture = myLectures.get(position);
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
        bgColor.setBackgroundColor(lecture.getLectureColor());
        fgColor.setBackgroundColor(lecture.getTextColor());

        classTimes = new ArrayList<>();
        for (JsonElement element : lecture.getClass_time_json()) {
            JsonObject jsonObject = element.getAsJsonObject();
            ClassTime classTime = new ClassTime(jsonObject);
            classTimes.add(classTime);
        }

        adapter = new ClassTimeAdapter(this, classTimes);
        timeListView.setAdapter(adapter);

        bgColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LectureDetailActivity.this, "bgColor clicked", Toast.LENGTH_SHORT).show();
                ColorPickerDialogBuilder
                        .with(LectureDetailActivity.this)
                        .setTitle("Choose color")
                        .initialColor(lecture.getLectureColor())
                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                        .density(12)
                        .setOnColorSelectedListener(new OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(int selectedColor) {
                                Toast.makeText(LectureDetailActivity.this, "onColorSelected: 0x" + Integer.toHexString(selectedColor),Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setPositiveButton("ok", new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                //changeBackgroundColor(selectedColor);
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .build()
                        .show();

            }
        });
        fgColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LectureDetailActivity.this, "fgColor clicked", Toast.LENGTH_SHORT).show();
                ColorPickerDialogBuilder
                        .with(LectureDetailActivity.this)
                        .setTitle("Choose color")
                        .initialColor(lecture.getTextColor())
                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                        .density(12)
                        .setOnColorSelectedListener(new OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(int selectedColor) {
                                Toast.makeText(LectureDetailActivity.this, "onColorSelected: 0x" + Integer.toHexString(selectedColor),Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setPositiveButton("ok", new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                //changeBackgroundColor(selectedColor);
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .build()
                        .show();


            }
        });
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
            String title = lecture.getCourse_title();
            String inst = lecture.getInstructor();
            JsonArray timeJson = adapter.getClassTimeJson();

            if (!Strings.isNullOrEmpty( course_title.getText().toString() )) {
                title = course_title.getText().toString();
            }
            if (!Strings.isNullOrEmpty( instructor.getText().toString() )) {
                inst = instructor.getText().toString();
            }

            LectureManager.getInstance().updateLecture(lecture, title, inst, timeJson);
            finish();
            return true;
        }  else if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
