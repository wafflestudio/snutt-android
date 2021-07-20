package com.wafflestudio.snutt2.views.logged_in.lecture_detail

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.ArrowKeyMovementMethod
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.SNUTTUtils
import com.wafflestudio.snutt2.handler.ApiOnError
import com.wafflestudio.snutt2.lib.getDefaultBgColorHex
import com.wafflestudio.snutt2.lib.getDefaultFgColorHex
import com.wafflestudio.snutt2.lib.rx.RxBindable
import com.wafflestudio.snutt2.manager.LectureManager
import com.wafflestudio.snutt2.model.LectureItem
import com.wafflestudio.snutt2.lib.network.dto.PutLectureParams
import com.wafflestudio.snutt2.lib.network.dto.core.ClassTimeDto
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.lib.network.dto.core.TableDto
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.kotlin.subscribeBy
import timber.log.Timber

/**
 * Created by makesource on 2017. 3. 17..
 */
class LectureDetailAdapter(
    private val lists: MutableList<LectureItem>,
    private val onSyllabus: () -> Unit,
    private val onRemoveLecture: () -> Unit,
    private val onResetLecture: () -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
    private var day = 0
    private var fromTime = 0
    private var toTime = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == LectureItem.ViewType.ItemShortHeader.value) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.cell_lecture_short_header, parent, false)
            return HeaderViewHolder(view)
        }
        if (viewType == LectureItem.ViewType.ItemLongHeader.value) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.cell_lecture_long_header, parent, false)
            return HeaderViewHolder(view)
        }
        if (viewType == LectureItem.ViewType.ItemClassTimeHeader.value) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.cell_lecture_class_header, parent, false)
            return HeaderViewHolder(view)
        }
        if (viewType == LectureItem.ViewType.ItemMargin.value) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.cell_lecture_margin, parent, false)
            return HeaderViewHolder(view)
        }
        if (viewType == LectureItem.ViewType.ItemTitle.value) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.cell_lecture_item_title, parent, false)
            return TitleViewHolder(view)
        }
        if (viewType == LectureItem.ViewType.ItemButton.value) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.cell_lecture_item_button, parent, false)
            return ButtonViewHolder(view)
        }
        if (viewType == LectureItem.ViewType.ItemColor.value) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.cell_lecture_item_color, parent, false)
            return ColorViewHolder(view)
        }
        if (viewType == LectureItem.ViewType.ItemClass.value) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.cell_lecture_item_class, parent, false)
            return ClassViewHolder(view)
        }
        if (viewType == LectureItem.ViewType.ItemRemark.value) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.cell_lecture_item_remark, parent, false)
            return RemarkViewHolder(view)
        }
        throw IllegalStateException("illegal viewType")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewType = getItemViewType(position)
        val item = getItem(position)
        if (viewType == LectureItem.ViewType.ItemTitle.value) {
            val viewHolder = holder as TitleViewHolder?
            viewHolder!!.bindData(item)
        }
        if (viewType == LectureItem.ViewType.ItemButton.value) {
            val viewHolder = holder as ButtonViewHolder?
            viewHolder!!.bindData(item) {
                when (item.type) {
                    LectureItem.Type.Syllabus -> onSyllabus()
                    LectureItem.Type.RemoveLecture -> onRemoveLecture()
                    LectureItem.Type.ResetLecture -> onResetLecture()
                    LectureItem.Type.AddClassTime -> {
                        addClassItem()
                        notifyItemInserted(lastClassItemPosition)
                    }
                    else -> {
                    }
                }
            }
        }
        if (viewType == LectureItem.ViewType.ItemColor.value) {
            val viewHolder = holder as ColorViewHolder?
            viewHolder!!.bindData(item)
        }
        if (viewType == LectureItem.ViewType.ItemClass.value) {
            val viewHolder = holder as ClassViewHolder?
            viewHolder!!.bindData(item) {
                if (item.isEditable) {
                    showDialog(item, it.context)
                }
            }
        }
        if (viewType == LectureItem.ViewType.ItemRemark.value) {
            val viewHolder = holder as RemarkViewHolder?
            viewHolder!!.bindData(item)
        }
    }

    private fun addClassItem() {
        val pos = lastClassItemPosition + 1
        lists.add(pos, LectureItem(ClassTimeDto(0, 0f, 1f, ""), LectureItem.Type.ClassTime, true))
    }

    private val lastClassItemPosition: Int
        private get() {
            for (i in 0 until itemCount) {
                if (isLastClassItem(i)) return i
            }
            return -1
        }

    private fun isLastClassItem(position: Int): Boolean {
        return if (position == itemCount - 1) false else getItem(position + 1).type === LectureItem.Type.AddClassTime
    }

    override fun getItemCount(): Int {
        return lists.size
    }

    override fun getItemViewType(position: Int): Int {
        return lists[position].viewType.value
    }

    fun getItem(position: Int): LectureItem {
        return lists[position]
    }

    fun getUpdateParam(): PutLectureParams {
        // 강의명, 교수, 학과, 학년, 학점, 분류, 구분, 강의시간 전체를 다 업데이트
        val target = PutLectureParams()
        val classTimeList = mutableListOf<ClassTimeDto>()

        for (item in lists) {
            val type = item.type
            when (type) {
                LectureItem.Type.Title -> target.course_title = item.value1
                LectureItem.Type.Instructor -> target.instructor = item.value1
                LectureItem.Type.Color -> {
                    target.colorIndex = item.colorIndex.toLong()
                    target.color = item.getColor()
                }
                LectureItem.Type.Department -> target.department = item.value1
                LectureItem.Type.AcademicYear -> target.academic_year = item.value1
                LectureItem.Type.Credit -> {
                    val value = getIntegerValue(item.value1)
                    target.credit = value.toLong()
                }
                LectureItem.Type.Classification -> target.classification = item.value1
                LectureItem.Type.Category -> target.category = item.value1
                LectureItem.Type.CourseNumber, LectureItem.Type.LectureNumber -> {
                }
                LectureItem.Type.ClassTime -> {
                    item.classTime?.let {
                        classTimeList.add(it)
                    }
                }
                LectureItem.Type.Remark -> target.remark = item.value1
                else -> {
                }
            }
        }
        target.class_time_json = classTimeList

        return target
    }

    private fun getIntegerValue(s: String?): Int {
        return try {
            s!!.toInt()
        } catch (e: Exception) {
            0
        }
    }

    inner class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view)
    inner class TitleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title: TextView
        private val value: EditText
        fun bindData(item: LectureItem) {
            title.text = item.title1
            value.setText(item.value1)
            value.isClickable = item.isEditable
            value.isFocusable = item.isEditable
            value.isFocusableInTouchMode = item.isEditable
            value.setTextColor(Color.BLACK)
            when (item.type) {
                LectureItem.Type.LectureNumber, LectureItem.Type.CourseNumber -> {
                    value.isClickable = false
                    value.isFocusable = false
                    value.isFocusableInTouchMode = false
                    value.setTextColor(Color.argb(if (item.isEditable) 51 else 255, 0, 0, 0))
                }
                else -> {
                    value.addTextChangedListener(
                        object : TextWatcher {
                            override fun beforeTextChanged(
                                s: CharSequence,
                                start: Int,
                                count: Int,
                                after: Int
                            ) {
                            }

                            override fun onTextChanged(
                                s: CharSequence,
                                start: Int,
                                before: Int,
                                count: Int
                            ) {
                            }

                            override fun afterTextChanged(s: Editable) {
                                textChangedListener!!.onText1Changed(s.toString(), position)
                            }
                        }
                    )
                    if (item.type === LectureItem.Type.Credit) { // 학점
                        value.inputType = InputType.TYPE_CLASS_NUMBER
                    }
                }
            }
            when (item.type) {
                LectureItem.Type.Credit -> {
                    value.inputType = InputType.TYPE_CLASS_NUMBER
                    value.hint = "0"
                }
                LectureItem.Type.Title -> {
                    value.inputType = InputType.TYPE_CLASS_TEXT
                    value.hint = if (item.isEditable) "예) 기초 영어" else "(없음)"
                }
                LectureItem.Type.Instructor -> {
                    value.inputType = InputType.TYPE_CLASS_TEXT
                    value.hint = if (item.isEditable) "예) 홍길동" else "(없음)"
                }
                else -> {
                    value.inputType = InputType.TYPE_CLASS_TEXT
                    value.hint = "(없음)"
                }
            }
        }

        init {
            title = view.findViewById<View>(R.id.text_title) as TextView
            value = view.findViewById<View>(R.id.text_value) as EditText
        }
    }

    inner class ButtonViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val layout: FrameLayout
        private val textView: TextView
        fun bindData(item: LectureItem, listener: View.OnClickListener) {
            layout.setOnClickListener(listener)
            when (item.type) {
                LectureItem.Type.Syllabus -> {
                    textView.text = "강의계획서"
                    textView.setTextColor(Color.parseColor("#000000"))
                }
                LectureItem.Type.RemoveLecture -> {
                    textView.text = "삭제"
                    textView.setTextColor(Color.parseColor("#FF0000"))
                }
                LectureItem.Type.AddClassTime -> {
                    textView.text = "+ 시간 및 장소 추가"
                    textView.setTextColor(Color.argb(154, 0, 0, 0))
                }
                LectureItem.Type.ResetLecture -> {
                    textView.text = "초기화"
                    textView.setTextColor(Color.parseColor("#FF0000"))
                }
                else -> {
                }
            }
        }

        init {
            layout = view.findViewById<View>(R.id.layout) as FrameLayout
            textView = view.findViewById<View>(R.id.text_button) as TextView
        }
    }

    inner class ColorViewHolder(val view: View) :
        RecyclerView.ViewHolder(view) {
        private val layout: LinearLayout
        private val title: TextView
        private val fgColor: View
        private val bgColor: View
        private val arrow: View

        fun bindData(item: LectureItem) {
            title.text = "색상"

            if (item.colorIndex > 0) {
                bgColor.setBackgroundColor(item.colorIndex.toLong().getDefaultBgColorHex())
                fgColor.setBackgroundColor(item.colorIndex.toLong().getDefaultFgColorHex())
            } else {
                bgColor.setBackgroundColor(item.getColor()!!.bgColor!!)
                fgColor.setBackgroundColor(item.getColor()!!.fgColor!!)
            }
            arrow.visibility = if (item.isEditable) View.VISIBLE else View.GONE
            layout.setOnClickListener {
                ColorPickerDialog.Builder(view.context)
                    .setTitle("색상 선택")
                    .setPositiveButton(R.string.common_ok, object : ColorEnvelopeListener {
                        override fun onColorSelected(envelope: ColorEnvelope?, fromUser: Boolean) {
                            Timber.d(envelope?.color.toString())
                        }
                    })
                    .show()
            }
        }

        init {
            layout = view.findViewById<View>(R.id.layout) as LinearLayout
            title = view.findViewById<View>(R.id.text_title) as TextView
            fgColor = view.findViewById(R.id.fgColor) as View
            bgColor = view.findViewById(R.id.bgColor) as View
            arrow = view.findViewById(R.id.arrow)
        }
    }

    inner class RemarkViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title1: TextView
        private val editText1: EditText
        fun bindData(item: LectureItem) {
            editText1.setText(item.value1)
            editText1.addTextChangedListener(
                object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                    }

                    override fun afterTextChanged(s: Editable) {
                        textChangedListener!!.onText1Changed(s.toString(), position)
                    }
                }
            )
            editText1.hint = if (item.isEditable) "비고를 입력해주세요" else "(없음)"
            editText1.movementMethod = null
            editText1.isClickable = item.isEditable
            editText1.isFocusable = item.isEditable
            editText1.isFocusableInTouchMode = item.isEditable
            editText1.movementMethod =
                if (item.isEditable) ArrowKeyMovementMethod.getInstance() else LinkMovementMethod.getInstance()
        }

        init {
            title1 = view.findViewById<View>(R.id.text_title) as TextView
            editText1 = view.findViewById<View>(R.id.text_value) as EditText
        }
    }

    inner class ClassViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener,
        OnLongClickListener {
        private val title1: TextView
        private val title2: TextView
        private val editText1: EditText
        private val editText2: EditText
        private val remove: LinearLayout
        fun bindData(item: LectureItem, listener: View.OnClickListener) {
            title1.hint = "시간"
            val time = SNUTTUtils.numberToWday(item.classTime!!.day) + " " +
                SNUTTUtils.numberToTime(item.classTime!!.start) + "~" +
                SNUTTUtils.numberToTime(item.classTime!!.start + item.classTime!!.len)
            editText1.setText(time)
            editText1.isClickable = false
            editText1.isFocusable = false
            editText1.setOnClickListener(listener)
            title2.hint = "장소"
            editText2.setText(item.classTime!!.place)
            editText2.addTextChangedListener(
                object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                    }

                    override fun afterTextChanged(s: Editable) {
                        textChangedListener!!.onLocationChanged(s.toString(), position)
// item.getClassTime().setPlace(s.toString());
                    }
                }
            )
            editText2.isClickable = item.isEditable
            editText2.isFocusable = item.isEditable
            editText2.isFocusableInTouchMode = item.isEditable
            remove.visibility = if (item.isEditable) View.VISIBLE else View.GONE
        }

        override fun onLongClick(v: View): Boolean {
            if (deleteClickListener != null) {
                deleteClickListener!!.onDeleteClick(v, position)
                return true
            }
            return false
        }

        override fun onClick(v: View) {
            if (deleteClickListener != null) {
                deleteClickListener!!.onDeleteClick(v, position)
            }
        }

        init {
            title1 = view.findViewById<View>(R.id.input_title1) as TextView
            title2 = view.findViewById<View>(R.id.input_title2) as TextView
            editText1 = view.findViewById<View>(R.id.input_time) as EditText
            editText2 = view.findViewById<View>(R.id.input_location) as EditText
            remove = view.findViewById<View>(R.id.remove) as LinearLayout
            title1.setOnLongClickListener(this)
            editText1.setOnLongClickListener(this)
            title2.setOnLongClickListener(this)
            editText2.setOnLongClickListener(this)
            view.setOnLongClickListener(this)
            remove.setOnClickListener(this)
        }
    }

    private fun showDialog(item: LectureItem, context: Context) {
        val alert = AlertDialog.Builder(context)
        alert.setPositiveButton("확인") { dialog, which -> // item's class time update
            val t =
                ClassTimeDto(day, fromTime / 2f, (toTime - fromTime) / 2f, item.classTime!!.place)
            item.classTime = t
            notifyDataSetChanged()
            dialog.dismiss()
        }.setNegativeButton("취소") { dialog, which -> dialog.dismiss() }
        val inflater = LayoutInflater.from(context)
        val layout = inflater.inflate(R.layout.dialog_time_picker, null)
        alert.setView(layout)
        alert.show()
        val dayPicker = layout.findViewById<View>(R.id.dayPicker) as NumberPicker
        val fromPicker = layout.findViewById<View>(R.id.timeFrom) as NumberPicker
        val toPicker = layout.findViewById<View>(R.id.timeTo) as NumberPicker
        day = item.classTime!!.day
        val days = arrayOf("월", "화", "수", "목", "금", "토", "일")
        dayPicker.minValue = 0
        dayPicker.maxValue = 6
        dayPicker.displayedValues = days
        dayPicker.value = day
        dayPicker.wrapSelectorWheel = false
        dayPicker.setOnValueChangedListener { picker, oldVal, newVal -> day = newVal }

        // used integer interval (origin value * 2) to use number picker
        fromTime = (item.classTime!!.start * 2).toInt()
        val from = SNUTTUtils.getTimeList(0, 27)
        fromPicker.minValue = 0
        fromPicker.maxValue = 27
        fromPicker.displayedValues = from
        fromPicker.value = fromTime
        fromPicker.wrapSelectorWheel = false
        fromPicker.setOnValueChangedListener { picker, oldVal, newVal ->
            fromTime = newVal
            /* set DisplayedValues as null to avoid out of bound index error */toPicker.displayedValues =
            null
            toPicker.value = fromTime + 1
            toPicker.minValue = fromTime + 1
            toPicker.maxValue = 28
            toPicker.displayedValues = SNUTTUtils.getTimeList(fromTime + 1, 28)
            /* setValue method does not call listener, so we have to change the value manually */toTime =
            fromTime + 1
        }
        toTime = (item.classTime!!.start + item.classTime!!.len).toInt() * 2
        val to = SNUTTUtils.getTimeList(fromTime + 1, 28)
        toPicker.minValue = fromTime + 1
        toPicker.maxValue = 28
        toPicker.displayedValues = to
        toPicker.value = toTime
        toPicker.wrapSelectorWheel = false
        toPicker.setOnValueChangedListener { picker, oldVal, newVal -> toTime = newVal }
    }

    private fun showDeleteDialog(position: Int, context: Context) {
        val alert = AlertDialog.Builder(context)
        alert.setPositiveButton("확인") { dialog, which ->
            lists.removeAt(position)
            notifyItemRemoved(position)
            dialog.dismiss()
        }.setNegativeButton("취소") { dialog, which -> dialog.dismiss() }.setTitle("시간을 삭제하시겠습니까?")
        alert.show()
    }


    private interface TextChangedListener {
        fun onText1Changed(text: String, position: Int)
        fun onText2Changed(text: String?, position: Int)
        fun onLocationChanged(text: String?, position: Int)
    }

    private fun setOnTextChangedListener(_textChangedListener: TextChangedListener) {
        textChangedListener = _textChangedListener
    }

    private interface DeleteClickListener {
        fun onDeleteClick(view: View?, position: Int)
    }

    private fun setOnDeleteClickListener(listener: DeleteClickListener) {
        deleteClickListener = listener
    }

    companion object {
        private const val TAG = "LECTURE_DETAIL_ADAPTER"
        private var textChangedListener: TextChangedListener? = null
        private var deleteClickListener: DeleteClickListener? = null
    }

    init {
        setOnTextChangedListener(
            object : TextChangedListener {
                override fun onText1Changed(text: String, position: Int) {
                    getItem(position).value1 = text
                }

                override fun onText2Changed(text: String?, position: Int) {
                    getItem(position).value2 = text
                }

                override fun onLocationChanged(text: String?, position: Int) {
                    getItem(position).classTime = getItem(position).classTime?.copy(
                        place = text
                            ?: ""
                    )
                }
            }
        )
        setOnDeleteClickListener(
            object : DeleteClickListener {
                override fun onDeleteClick(view: View?, position: Int) {
                    showDeleteDialog(position, view?.context!!)
                }
            }
        )
    }
}
