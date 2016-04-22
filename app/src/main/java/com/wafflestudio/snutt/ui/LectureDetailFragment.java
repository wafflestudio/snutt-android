package com.wafflestudio.snutt.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.wafflestudio.snutt.R;
import com.wafflestudio.snutt.SNUTTBaseFragment;
import com.wafflestudio.snutt.manager.LectureManager;
import com.wafflestudio.snutt.model.ClassTime;
import com.wafflestudio.snutt.model.Lecture;
import com.wafflestudio.snutt.ui.adapter.ClassTimeAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by makesource on 2016. 4. 5..
 */
public class LectureDetailFragment extends SNUTTBaseFragment {


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

    public static LectureDetailFragment newInstance() {
        return new LectureDetailFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_lecture_detail, container, false);

        timeListView = (ListView) rootView.findViewById(R.id.time_listView);
        classification = (TextView) rootView.findViewById(R.id.classification);
        department = (TextView) rootView.findViewById(R.id.department);
        academic_year = (TextView) rootView.findViewById(R.id.academic_year);
        course_number = (TextView) rootView.findViewById(R.id.course_number);
        lecture_number = (TextView) rootView.findViewById(R.id.lecture_number);
        course_title = (EditText) rootView.findViewById(R.id.course_title);
        credit = (TextView) rootView.findViewById(R.id.credit);
        class_time = (TextView) rootView.findViewById(R.id.class_time);
        instructor = (EditText) rootView.findViewById(R.id.instructor);
        remark = (TextView) rootView.findViewById(R.id.remark);
        bgColor = (View) rootView.findViewById(R.id.bgColor);
        fgColor = (View) rootView.findViewById(R.id.fgColor);

        myLectures = LectureManager.getInstance().getLectures();

        Intent intent = getActivity().getIntent();
        int position = intent.getIntExtra(INTENT_KEY_LECTURE_POSITION, -1);

        // throws exception when the list position is out of range
        Preconditions.checkPositionIndex(position, myLectures.size());

        lecture = myLectures.get(position);

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
        bgColor.setBackgroundColor(lecture.getBgColor());
        fgColor.setBackgroundColor(lecture.getFgColor());

        classTimes = new ArrayList<>();
        for (JsonElement element : lecture.getClass_time_json()) {
            JsonObject jsonObject = element.getAsJsonObject();
            ClassTime classTime = new ClassTime(jsonObject);
            classTimes.add(classTime);
        }

        adapter = new ClassTimeAdapter(getContext(), classTimes);
        timeListView.setAdapter(adapter);

        bgColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LectureMainActivity)getActivity()).setColorPickerFragment();
            }
        });

        /*bgColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LectureMainActivity.this, "bgColor clicked", Toast.LENGTH_SHORT).show();
                ColorPickerDialogBuilder
                        .with(LectureMainActivity.this)
                        .setTitle("Choose color")
                        .initialColor(lecture.getLectureColor())
                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                        .density(12)
                        .setOnColorSelectedListener(new OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(int selectedColor) {
                                Toast.makeText(LectureMainActivity.this, "onColorSelected: 0x" + Integer.toHexString(selectedColor),Toast.LENGTH_SHORT).show();
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
                Toast.makeText(LectureMainActivity.this, "fgColor clicked", Toast.LENGTH_SHORT).show();
                ColorPickerDialogBuilder
                        .with(LectureMainActivity.this)
                        .setTitle("Choose color")
                        .initialColor(lecture.getTextColor())
                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                        .density(12)
                        .setOnColorSelectedListener(new OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(int selectedColor) {
                                Toast.makeText(LectureMainActivity.this, "onColorSelected: 0x" + Integer.toHexString(selectedColor),Toast.LENGTH_SHORT).show();
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
        });*/
        return rootView;
    }

}
