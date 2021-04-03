package com.wafflestudio.snutt2.adapter

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.EditText
import android.widget.TextView
import com.google.common.base.Strings
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.SNUTTUtils
import com.wafflestudio.snutt2.model.ClassTime
import java.util.*

/**
 * Created by makesource on 2016. 3. 6..
 */
class ClassTimeAdapter(private val context: Context, private val times: List<ClassTime>) : BaseAdapter() {
    private val places: MutableList<String>
    private var watcher: MyWatcher? = null
    override fun getCount(): Int {
        return times.size
    }

    override fun getItem(position: Int): Any {
        return times[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View, parent: ViewGroup): View {
        var v = convertView
        if (v == null) {
            val inflater = context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            v = inflater.inflate(R.layout.cell_time, null)
        }
        val classTime = times[position]
        val tv_time = v.findViewById<View>(R.id.class_time) as TextView
        val et_place = v.findViewById<View>(R.id.class_place) as EditText
        val time = SNUTTUtils.numberToWday(classTime.day) + " " +
            SNUTTUtils.numberToTime(classTime.start) + "~" +
            SNUTTUtils.numberToTime(classTime.start + classTime.len)
        tv_time.text = time
        if (watcher != null) et_place.removeTextChangedListener(watcher)
        et_place.setText(places[position])
        et_place.hint = classTime.place
        watcher = MyWatcher(position)
        et_place.addTextChangedListener(watcher)
        Log.d(TAG, "getView is called : $position")
        return v
    }

    val classTimeJson: JsonArray
        get() {
            val ja = JsonArray()
            for (i in times.indices) {
                val time = times[i]
                val `object` = JsonObject()
                `object`.addProperty("day", time.day)
                `object`.addProperty("start", time.start)
                `object`.addProperty("len", time.len)
                `object`.addProperty("_id", time.get_id())
                if (!Strings.isNullOrEmpty(places[i])) {
                    `object`.addProperty("place", places[i])
                } else {
                    `object`.addProperty("place", time.place)
                }
                ja.add(`object`)
            }
            return ja
        }

    private inner class MyWatcher(private val position: Int) : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable) {
            val text = s.toString()
            Log.d(TAG, "$position : $text")
            places[position] = text
        }
    }

    companion object {
        private const val TAG = "CLASS_TIME_ADAPTER"
    }

    init {
        places = ArrayList()
        for (time in times) {
            places.add(time.place)
        }
    }
}
