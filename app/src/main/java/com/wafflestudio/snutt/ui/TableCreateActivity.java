package com.wafflestudio.snutt.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;

import com.wafflestudio.snutt.R;
import com.wafflestudio.snutt.SNUTTBaseActivity;
import com.wafflestudio.snutt.manager.TableManager;
import com.wafflestudio.snutt.model.Lecture;
import com.wafflestudio.snutt.model.Table;

import java.util.ArrayList;

/**
 * Created by makesource on 2016. 3. 1..
 */
public class TableCreateActivity extends SNUTTBaseActivity {

    private int year = 2014;
    private int semester = 1;
    private EditText titleText;
    private NumberPicker yearPicker;
    private NumberPicker semesterPicker;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_create);

        yearPicker = (NumberPicker) findViewById(R.id.yearPicker);
        semesterPicker = (NumberPicker) findViewById(R.id.semesterPicker);
        titleText = (EditText) findViewById(R.id.tableName);

        String[] years = { "2014 년", "2015 년", "2016 년" };
        yearPicker.setMinValue(2014);
        yearPicker.setMaxValue(2016);
        yearPicker.setDisplayedValues(years);
        yearPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                year = newVal;
            }
        });

        final String[] semesters = { "1학기", "여름학기", "2학기", "겨울학기" };
        semesterPicker.setMinValue(1);
        semesterPicker.setMaxValue(4);
        semesterPicker.setDisplayedValues(semesters);
        semesterPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                semester = newVal;
            }
        });

        submitButton = (Button) findViewById(R.id.submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO : (SeongWon) server에 새로운 table생성 요청 보내기, MainActivity가 들고있는 방식 다시 생각해보기
                // 나중에는 server callback에서 화면 전환하는 방식으로 바꾸기
                // 없는 수강편람일수도, 그리고 인터넷 연결이 되지 않았을 때도 넘어가면 안되기 때문
                String title = titleText.getText().toString();
                String id = String.valueOf(year) + String.valueOf(semester) + title ;
                Table table = new Table(id,year,semester,title,new ArrayList<Lecture>());
                TableManager.getInstance().addTable(table);
                startTableView(id);
                finish();
            }
        });

    }
}
