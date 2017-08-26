package com.wafflestudio.snutt_staging.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * Created by makesource on 2017. 8. 27..
 */

public class ToggleRadioButton extends android.support.v7.widget.AppCompatRadioButton {

    public ToggleRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void toggle() {
        if(isChecked()) {
            if(getParent() instanceof RadioGroup) {
                ((RadioGroup)getParent()).clearCheck();
            }
        } else {
            setChecked(true);
        }
    }
}