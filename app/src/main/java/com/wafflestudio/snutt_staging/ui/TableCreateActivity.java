package com.wafflestudio.snutt_staging.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.wafflestudio.snutt_staging.R;
import com.wafflestudio.snutt_staging.SNUTTBaseActivity;
import com.wafflestudio.snutt_staging.manager.TableManager;
import com.wafflestudio.snutt_staging.model.Table;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

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
        activityList.add(this);
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
                String title = titleText.getText().toString();
                TableManager.getInstance().postTable(year, semester, title, new Callback<List<Table>>() {
                    @Override
                    public void success(List<Table> tables, Response response) {
                        // 보고있는 테이블 정보 변경하기
                        //startTableView(id);
                        finish();
                    }
                    @Override
                    public void failure(RetrofitError error) {
                        Toast.makeText(TableCreateActivity.this, "테이블 생성에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityList.remove(this);
    }
}
