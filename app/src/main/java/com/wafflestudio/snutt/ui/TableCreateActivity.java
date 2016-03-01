package com.wafflestudio.snutt.ui;

import android.os.Bundle;
import android.widget.NumberPicker;

import com.wafflestudio.snutt.R;
import com.wafflestudio.snutt.SNUTTBaseActivity;

/**
 * Created by makesource on 2016. 3. 1..
 */
public class TableCreateActivity extends SNUTTBaseActivity {

    private NumberPicker yearPicker;
    private NumberPicker semesterPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_create);

        yearPicker = (NumberPicker) findViewById(R.id.yearPicker);
        semesterPicker = (NumberPicker) findViewById(R.id.semesterPicker);

        String[] years = { "2014 년", "2015 년", "2016 년" };
        yearPicker.setMinValue(2014);
        yearPicker.setMaxValue(2016);
        yearPicker.setDisplayedValues(years);
        yearPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

            }
        });

        String[] semesters = { "1학기", "여름학기", "2학기", "겨울학기" };
        semesterPicker.setMinValue(1);
        semesterPicker.setMaxValue(4);
        semesterPicker.setDisplayedValues(semesters);
        semesterPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

            }
        });


    }
}
