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
import com.wafflestudio.snutt_staging.model.Coursebook;
import com.wafflestudio.snutt_staging.model.Table;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by makesource on 2016. 3. 1..
 */
public class TableCreateActivity extends SNUTTBaseActivity {

    private int year = -1;
    private int semester = -1;
    private EditText titleText;
    private NumberPicker semesterPicker;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityList.add(this);
        setContentView(R.layout.activity_table_create);
        setTitle("새로운 시간표");

        semesterPicker = (NumberPicker) findViewById(R.id.semesterPicker);
        titleText = (EditText) findViewById(R.id.tableName);

        TableManager.getInstance().getCoursebook(new Callback<List<Coursebook>>() {
            @Override
            public void success(List<Coursebook> coursebooks, Response response) {
                String[] displays = getDisplayList(coursebooks);
                final int[] years = getYearList(coursebooks);
                final int[] semesters = getSemesterList(coursebooks);
                int size = coursebooks.size();
                year = years[0];
                semester = semesters[0];
                semesterPicker.setMinValue(0);
                semesterPicker.setMaxValue(size - 1);
                semesterPicker.setDisplayedValues(displays);
                semesterPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        year = years[newVal];
                        semester = semesters[newVal];
                    }
                });
            }
            @Override
            public void failure(RetrofitError error) {
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

    private String[] getDisplayList(List<Coursebook> coursebooks) {
        int size = coursebooks.size();
        String[] list = new String[size];
        for (int i = 0;i < size;i ++) {
            String year = coursebooks.get(i).getYear() + " 년";
            String semester = getSemester(coursebooks.get(i).getSemester());
            list[i] = year + " " + semester;
        }
        return list;
    }

    private String getSemester(int semester) {
        switch (semester) {
            case 1:
                return "1학기";
            case 2:
                return "여름학기";
            case 3:
                return "2학기";
            case 4:
                return "겨울학기";
            default:
                return "(null)";
        }
    }

    private int[] getYearList(List<Coursebook> coursebooks) {
        int size = coursebooks.size();
        int[] list = new int[size];
        for (int i = 0;i < size;i ++) {
            list[i] = coursebooks.get(i).getYear();
        }
        return list;
    }

    private int[] getSemesterList(List<Coursebook> coursebooks) {
        int size = coursebooks.size();
        int[] list = new int[size];
        for (int i = 0;i < size;i ++) {
            list[i] = coursebooks.get(i).getSemester();
        }
        return list;
    }

}
