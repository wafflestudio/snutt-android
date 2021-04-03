package com.wafflestudio.snutt2.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.wafflestudio.snutt2.R;
import com.wafflestudio.snutt2.SNUTTBaseActivity;
import com.wafflestudio.snutt2.manager.TableManager;
import com.wafflestudio.snutt2.model.Coursebook;
import com.wafflestudio.snutt2.model.Table;

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
    private Spinner semesterSpinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityList.add(this);
        setContentView(R.layout.activity_table_create);
        setTitle("새로운 시간표");

        semesterSpinner = (Spinner) findViewById(R.id.spinner);
        titleText = (EditText) findViewById(R.id.table_title);

        TableManager.getInstance().getCoursebook(new Callback<List<Coursebook>>() {
            @Override
            public void success(List<Coursebook> coursebooks, Response response) {
                String[] displays = getDisplayList(coursebooks);
                final int[] years = getYearList(coursebooks);
                final int[] semesters = getSemesterList(coursebooks);
                year = years[0];
                semester = semesters[0];
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item, displays);
                semesterSpinner.setAdapter(adapter);
                semesterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        year = years[position];
                        semester = semesters[position];
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
            @Override
            public void failure(RetrofitError error) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_table_create, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_create) {
            String title = titleText.getText().toString();
            TableManager.getInstance().postTable(year, semester, title, new Callback<List<Table>>() {
                @Override
                public void success(List<Table> tables, Response response) {
                    finish();
                }
                @Override
                public void failure(RetrofitError error) {
                }
            });
            return true;
        }

        return super.onOptionsItemSelected(item);
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
