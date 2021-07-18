package com.wafflestudio.snutt2.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.wafflestudio.snutt2.ColorConst
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.manager.LectureManager
import com.wafflestudio.snutt2.lib.network.dto.core.ColorDto

/**
 * Created by makesource on 2017. 5. 28..
 */
class ColorListAdapter(
    private val colorList: List<ColorDto>,
    private val colorNameList: List<String>,
    private val lectureManager: LectureManager,
    index: Int
) : BaseAdapter() {
    private val selected: Int
    override fun getCount(): Int {
        return colorList.size + 1
    }

    // 뭐지... 일단 그대로 나두기
    override fun getItem(position: Int): Any {
        return Any()
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val v = convertView ?: LayoutInflater.from(parent.context)
            .inflate(R.layout.cell_lecture_color, parent, false)
        val nameText = v.findViewById<View>(R.id.name) as TextView
        val fgColor = v.findViewById<View>(R.id.fgColor)
        val bgColor = v.findViewById<View>(R.id.bgColor)
        val checked = v.findViewById<View>(R.id.checked) as ImageView
        checked.visibility = if (position == selected) View.VISIBLE else View.INVISIBLE
        if (position == colorList.size) { // for custom color
            nameText.text = ColorConst.defaultColorName
            fgColor.setBackgroundColor(ColorConst.defaultFgColor)
            bgColor.setBackgroundColor(ColorConst.defaultBgColor)
        } else {
            val color = colorList[position]
            val name = colorNameList[position]
            nameText.text = name
            fgColor.setBackgroundColor(color.fgColor!!)
            bgColor.setBackgroundColor(color.bgColor!!)
        }
        return v
    }

    companion object {
        private const val TAG = "COLOR_LIST_ADAPTER"
    }

    init {
        selected = if (index == 0) colorList.size else index - 1
    }
}
