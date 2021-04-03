package com.wafflestudio.snutt2.view

import android.content.Context
import android.util.AttributeSet
import android.widget.RadioGroup
import androidx.appcompat.widget.AppCompatRadioButton

/**
 * Created by makesource on 2017. 8. 27..
 */
class ToggleRadioButton(context: Context?, attrs: AttributeSet?) : AppCompatRadioButton(context, attrs) {
    override fun toggle() {
        if (isChecked) {
            if (parent is RadioGroup) {
                (parent as RadioGroup).clearCheck()
            }
        } else {
            isChecked = true
        }
    }
}