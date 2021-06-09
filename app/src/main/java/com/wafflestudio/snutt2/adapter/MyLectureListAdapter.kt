package com.wafflestudio.snutt2.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.common.base.Strings
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.SNUTTUtils.displayWidth
import com.wafflestudio.snutt2.SNUTTUtils.dp2px
import com.wafflestudio.snutt2.lib.network.SNUTTStringUtils
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
/**
 * Created by makesource on 2016. 2. 23..
 */
class MyLectureListAdapter(private val myLecture: List<LectureDto>?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cellLayoutView: View
        val viewHolder: ViewHolder
        cellLayoutView = LayoutInflater.from(parent.context)
            .inflate(R.layout.cell_my_lecture, parent, false)
        // create ViewHolder
        viewHolder = ViewHolder(cellLayoutView)
        return viewHolder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val lecture = myLecture!![position]
        val viewHolder = holder as ViewHolder
        viewHolder.bindData(lecture)
    }

    override fun getItemCount(): Int {
        return myLecture?.size ?: 0
    }

    // inner class to hold a reference to each item of RecyclerView
    class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, OnLongClickListener {
        private val title: TextView
        private val subTitle: TextView
        private val tag: TextView
        private val classTime: TextView
        private val location: TextView
        fun bindData(lecture: LectureDto) {
            val titleText = lecture.course_title
            val subTitleText = "(" + lecture.instructor + " / " + java.lang.String.valueOf(
                lecture.credit
            ) + "학점)"
            title.text = titleText
            subTitle.text = subTitleText
            val maxWidth = (itemView.context.displayWidth - itemView.context.dp2px((20 + 20 + 10).toFloat())).toInt()
            var subTitleWidth = Math.min(getTextViewWidth(subTitle), (maxWidth / 2).toFloat()).toInt()
            val titleWidth = Math.min(getTextViewWidth(title), (maxWidth - subTitleWidth).toFloat()).toInt()
            if (titleWidth + subTitleWidth < maxWidth) {
                subTitleWidth = maxWidth - titleWidth
            }
            subTitle.layoutParams = LinearLayout.LayoutParams(
                subTitleWidth,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            title.layoutParams = LinearLayout.LayoutParams(
                titleWidth,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            var tagText: String? = ""
            lecture.category?.let {
                tagText += "$it, "
            }
            lecture.department?.let {
                tagText += "$it, "
            }
            lecture.academic_year?.let {
                tagText += "$it, "
            }
            if (Strings.isNullOrEmpty(tagText)) tagText = "(없음)"
            tag.text = tagText
            var classTimeText = SNUTTStringUtils.getSimplifiedClassTime(lecture)
            if (Strings.isNullOrEmpty(classTimeText)) classTimeText = "(없음)"
            classTime.text = classTimeText
            var locationText = SNUTTStringUtils.getSimplifiedLocation(lecture)
            if (Strings.isNullOrEmpty(locationText)) locationText = "(없음)"
            location.text = locationText
        }

        private fun getTextViewWidth(textView: TextView): Float {
            textView.measure(0, 0)
            return textView.measuredWidth.toFloat()
        }

        override fun onClick(v: View) {
            if (clickListener != null) {
                clickListener!!.onClick(v, position)
            }
        }

        override fun onLongClick(v: View): Boolean {
            if (longClickListener != null) {
                longClickListener!!.onLongClick(v, position)
            }
            return true
        }

        init {
            title = itemView.findViewById<View>(R.id.title) as TextView
            subTitle = itemView.findViewById<View>(R.id.sub_title) as TextView
            tag = itemView.findViewById<View>(R.id.tag) as TextView
            classTime = itemView.findViewById<View>(R.id.time) as TextView
            location = itemView.findViewById<View>(R.id.location) as TextView
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }
    }

    interface ClickListener {
        fun onClick(v: View?, position: Int)
    }

    interface LongClickListener {
        fun onLongClick(v: View?, position: Int)
    }

    fun setOnItemClickListener(_clickListener: ClickListener) {
        clickListener = _clickListener
    }

    fun setOnItemLongClickListener(_longClickListener: LongClickListener) {
        longClickListener = _longClickListener
    }

    companion object {
        private const val TAG = "MY_LECTURE_LIST_ADAPTER"
        private var clickListener: ClickListener? = null
        private var longClickListener: LongClickListener? = null
    }
}
