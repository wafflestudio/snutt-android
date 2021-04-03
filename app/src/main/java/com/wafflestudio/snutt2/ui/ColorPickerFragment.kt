package com.wafflestudio.snutt2.ui

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.SNUTTBaseFragment
import com.wafflestudio.snutt2.adapter.ColorListAdapter
import com.wafflestudio.snutt2.manager.LectureManager.Companion.instance
import com.wafflestudio.snutt2.model.Color

/**
 * Created by makesource on 2016. 4. 5..
 */
class ColorPickerFragment : SNUTTBaseFragment() {
    private var listView: ListView? = null

    // Activity 로 데이터를 전달할 커스텀 리스너
    private var mCallback: ColorChangedListener? = null

    // Activity 로 데이터를 전달할 커스텀 리스너의 인터페이스
    interface ColorChangedListener {
        fun onColorChanged(index: Int, color: Color?)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_color_picker, container, false)
        val colors = instance!!.colorList
        val names = instance!!.colorNameList
        val index = arguments!!.getInt("index")
        listView = rootView.findViewById<View>(R.id.listView) as ListView
        listView!!.adapter = ColorListAdapter(colors!!, names!!, index)
        listView!!.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            if (position == colors.size) {
                ColorPickerDialogBuilder
                        .with(context)
                        .setTitle("배경색")
                        .initialColor(instance!!.defaultBgColor)
                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                        .density(12)
                        .setOnColorSelectedListener { selectedColor ->
                            Log.d(TAG, "onColorSelected: 0x" + Integer.toHexString(selectedColor))
                            //Toast.makeText(getContext(), "onColorSelected: 0x" + Integer.toHexString(selectedColor),Toast.LENGTH_SHORT).show();
                        }
                        .setPositiveButton("ok") { dialog, selectedColor, allColors ->
                            val bgColor = selectedColor
                            ColorPickerDialogBuilder
                                    .with(context)
                                    .setTitle("텍스트색")
                                    .initialColor(instance!!.defaultFgColor)
                                    .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                                    .density(12)
                                    .setOnColorSelectedListener { selectedColor -> Log.d(TAG, "onColorSelected: 0x" + Integer.toHexString(selectedColor)) }
                                    .setPositiveButton("ok") { dialog, selectedColor, allColors ->
                                        val fgColor = selectedColor
                                        mCallback!!.onColorChanged(0, Color(bgColor, fgColor))
                                        //LectureManager.getInstance().updateLecture(lecture, bgColor, fgColor);
                                        activity!!.onBackPressed()
                                    }
                                    .setNegativeButton("cancel") { dialog, which -> }
                                    .build()
                                    .show()
                        }
                        .setNegativeButton("cancel") { dialog, which -> }
                        .build()
                        .show()
            } else {
                mCallback!!.onColorChanged(position + 1, null)
                activity!!.onBackPressed()
            }
        }
        //setDefaultColor();
        //setListener();
        return rootView
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        mCallback = activity as ColorChangedListener
    }

    companion object {
        private const val TAG = "COLOR_PICKER_FRAGMENT"
        @JvmStatic
        fun newInstance(): ColorPickerFragment {
            return ColorPickerFragment()
        }
    }
}