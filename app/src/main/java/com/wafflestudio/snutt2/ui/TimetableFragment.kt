package com.wafflestudio.snutt2.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.SwitchCompat
import com.appyvet.rangebar.RangeBar
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.SNUTTBaseFragment
import com.wafflestudio.snutt2.SNUTTUtils.numberToWday
import com.wafflestudio.snutt2.manager.PrefStorage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by makesource on 2017. 1. 24..
 */
@AndroidEntryPoint
class TimetableFragment : SNUTTBaseFragment() {

    @Inject
    lateinit var prefStorage: PrefStorage

    private var mSwitch: SwitchCompat? = null
    private var dayLayout: LinearLayout? = null
    private var classLayout: LinearLayout? = null
    private var dayRangeBar: RangeBar? = null
    private var classRangeBar: RangeBar? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_timetable, container, false)
        mSwitch = rootView.findViewById<View>(R.id.switch1) as SwitchCompat
        dayLayout = rootView.findViewById<View>(R.id.day_layout) as LinearLayout
        classLayout = rootView.findViewById<View>(R.id.class_layout) as LinearLayout
        dayRangeBar = rootView.findViewById<View>(R.id.day_range_bar) as RangeBar
        classRangeBar = rootView.findViewById<View>(R.id.class_range_bar) as RangeBar
        mSwitch!!.isChecked = prefStorage.autoTrim
        initRangeBar()
        updateRangeBarStatus(prefStorage.autoTrim)
        mSwitch!!.setOnCheckedChangeListener { buttonView, isChecked ->
            Log.d(TAG, "on checked changed listener called.")
            prefStorage.autoTrim = isChecked
            updateRangeBarStatus(isChecked)
        }
        return rootView
    }

    private fun initRangeBar() {
        dayRangeBar!!.setRangePinsByIndices(
            prefStorage.trimWidthStart,
            prefStorage.trimWidthStart + prefStorage.trimWidthNum - 1
        )
        dayRangeBar!!.setFormatter { value ->
            val wday = value.toInt()
            numberToWday(wday)!!
        }
        dayRangeBar!!.setOnRangeBarChangeListener { rangeBar, leftPinIndex, rightPinIndex, leftPinValue, rightPinValue ->
            val num = rightPinIndex - leftPinIndex + 1
            prefStorage.trimWidthStart = leftPinIndex
            prefStorage.trimWidthNum = num
        }
        classRangeBar!!.setRangePinsByIndices(
            prefStorage.trimHeightStart,
            prefStorage.trimHeightStart + prefStorage.trimHeightNum - 1
        )
        classRangeBar!!.setOnRangeBarChangeListener { rangeBar, leftPinIndex, rightPinIndex, leftPinValue, rightPinValue ->
            val num = rightPinIndex - leftPinIndex + 1
            prefStorage.trimHeightStart = leftPinIndex
            prefStorage.trimHeightNum = num
        }
    }

    private fun updateRangeBarStatus(b: Boolean) {
        dayLayout!!.visibility = if (b) View.INVISIBLE else View.VISIBLE
        classLayout!!.visibility = if (b) View.INVISIBLE else View.VISIBLE
    }

    companion object {
        private const val TAG = "TIMETABLE_FRAGMENT"
    }
}
