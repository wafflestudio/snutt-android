package com.wafflestudio.snutt_staging.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.appyvet.rangebar.IRangeBarFormatter;
import com.appyvet.rangebar.RangeBar;
import com.wafflestudio.snutt_staging.R;
import com.wafflestudio.snutt_staging.SNUTTBaseFragment;
import com.wafflestudio.snutt_staging.SNUTTUtils;
import com.wafflestudio.snutt_staging.manager.PrefManager;

/**
 * Created by makesource on 2017. 1. 24..
 */

public class TimetableFragment extends SNUTTBaseFragment {
    private static final String TAG = "TIMETABLE_FRAGMENT";

    private Switch mSwitch;
    private RangeBar dayRangeBar;
    private RangeBar classRangeBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_timetable, container, false);
        mSwitch = (Switch) rootView.findViewById(R.id.switch1);
        dayRangeBar = (RangeBar) rootView.findViewById(R.id.day_range_bar);
        classRangeBar = (RangeBar) rootView.findViewById(R.id.class_range_bar);

        mSwitch.setChecked(PrefManager.getInstance().getAutoTrim());
        updateRangeBarStatus(PrefManager.getInstance().getAutoTrim());

        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, "on checked changed listener called.");
                PrefManager.getInstance().setAutoTrim(isChecked);
                updateRangeBarStatus(isChecked);
            }
        });

        return rootView;
    }

    private void updateRangeBarStatus(boolean b) {
        Log.d(TAG, "update range bar status called");
        dayRangeBar.setEnabled(!b);
        dayRangeBar.setRangePinsByIndices(PrefManager.getInstance().getTrimWidthStart(),
                PrefManager.getInstance().getTrimWidthStart() + PrefManager.getInstance().getTrimWidthNum() - 1);
        dayRangeBar.setFormatter(new IRangeBarFormatter() {
            @Override
            public String format(String value) {
                int wday = Integer.parseInt(value);
                return SNUTTUtils.numberToWday(wday);
            }
        });
        dayRangeBar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex, String leftPinValue, String rightPinValue) {
                int start = leftPinIndex;
                int num = rightPinIndex - leftPinIndex + 1;
                PrefManager.getInstance().setTrimWidthStart(start);
                PrefManager.getInstance().setTrimWidthNum(num);
            }
        });
        classRangeBar.setEnabled(!b);
        classRangeBar.setRangePinsByIndices(PrefManager.getInstance().getTrimHeightStart(),
                PrefManager.getInstance().getTrimHeightStart() + PrefManager.getInstance().getTrimHeightNum() - 1);
        classRangeBar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex, String leftPinValue, String rightPinValue) {
                int start = leftPinIndex;
                int num = rightPinIndex - leftPinIndex + 1;
                PrefManager.getInstance().setTrimHeightStart(start);
                PrefManager.getInstance().setTrimHeightNum(num);
            }
        });
    }

}
