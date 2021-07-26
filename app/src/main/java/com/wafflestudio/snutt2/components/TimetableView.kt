package com.wafflestudio.snutt2.components

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.lib.contains
import com.wafflestudio.snutt2.lib.getDefaultBgColorHex
import com.wafflestudio.snutt2.lib.getDefaultFgColorHex
import com.wafflestudio.snutt2.lib.network.dto.core.ClassTimeDto
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.lib.rx.dp
import com.wafflestudio.snutt2.lib.rx.sp
import com.wafflestudio.snutt2.lib.toDayString
import com.wafflestudio.snutt2.model.TableTrimParam
import timber.log.Timber
import kotlin.math.max
import kotlin.math.min

class TimetableView : View {

    private val hourLabelWidth = 24.5f.dp(context)
    private val dayLabelHeight = 28.5f.dp(context)
    private val cellPadding = 4.dp(context)
    private var onLectureClickListener: OnLectureClickListener? = null

    private val linePaint: Paint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(235, 235, 235)
            strokeWidth = 1.dp(context)
        }

    private val subLinePaint: Paint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(243, 243, 243)
            strokeWidth = 1.dp(context)
        }

    private val hourLabelTextPaint: Paint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(180, 0, 0, 0)
            textSize = 12.sp(context)
            textAlign = Paint.Align.RIGHT
            typeface = ResourcesCompat.getFont(context, R.font.spoqa_han_sans_light)
        }

    private val dayLabelTextPaint: Paint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(180, 0, 0, 0)
            textSize = 12.sp(context)
            textAlign = Paint.Align.CENTER
            typeface = ResourcesCompat.getFont(context, R.font.spoqa_han_sans_light)
        }

    private val lectureCellTextRect =
        TextRect(Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = 10f.sp(context)
            typeface = ResourcesCompat.getFont(context, R.font.spoqa_han_sans_regular)
        })

    private val lectureCellSubTextRect =
        TextRect(Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = 11f.sp(context)
            typeface = ResourcesCompat.getFont(context, R.font.spoqa_han_sans_bold)
        })

    var trimParam: TableTrimParam = TableTrimParam.Default
        set(value) {
            Timber.d(value.toString())
            field = value
            invalidate()
        }

    var lectures: List<LectureDto> = listOf()
        set(value) {
            field = value
            invalidate()
        }

    var selectedLecture: LectureDto? = null
        set(value) {
            field = value
            invalidate()
        }

    private val unitWidth: Float
        get() = (width - hourLabelWidth) / (trimParam.dayOfWeekTo - trimParam.dayOfWeekFrom + 1)

    private val unitHeight: Float
        get() = (height - dayLabelHeight) / (trimParam.hourTo - trimParam.hourFrom + 1)

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        setBackgroundColor(Color.rgb(255, 255, 255))
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawTableGrid(canvas)
        for (lecture in lectures) {
            drawLecture(canvas, lecture)
        }
        selectedLecture?.let {
            drawSelectedLecture(canvas, it)
        }
    }

    fun setOnLectureClickListener(listener: (lecture: LectureDto) -> Unit) {
        this.onLectureClickListener = object : OnLectureClickListener {
            override fun onClick(lecture: LectureDto) {
                listener(lecture)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        val day = ((x - hourLabelWidth) / unitWidth).toInt() + trimParam.dayOfWeekFrom
        val time = ((y - dayLabelHeight) / unitHeight) + trimParam.hourFrom - 8

        if (event.action == MotionEvent.ACTION_UP) {
            for (lec in lectures) {
                if (lec.contains(day, time)) {
                    this.onLectureClickListener?.onClick(lec)
                }
            }
        }
        return true
    }

    private fun drawTableGrid(canvas: Canvas) {
        val textHeight = getTextHeight(0.toDayString(context), dayLabelTextPaint)

        val verticalLines = trimParam.dayOfWeekTo - trimParam.dayOfWeekFrom + 1
        var startWidth = hourLabelWidth
        repeat(verticalLines) {
            canvas.drawLine(startWidth, 0f, startWidth, height.toFloat(), linePaint)
            canvas.drawText(
                (trimParam.dayOfWeekFrom + it).toDayString(context),
                startWidth + unitWidth * 0.5f,
                (dayLabelHeight + textHeight) / 2f,
                dayLabelTextPaint
            )
            startWidth += unitWidth
        }


        val horizontalLines = trimParam.hourTo - trimParam.hourFrom + 1
        var startHeight = dayLabelHeight
        repeat(horizontalLines) {
            canvas.drawLine(0f, startHeight, width.toFloat(), startHeight, linePaint)
            canvas.drawText(
                (trimParam.hourFrom + it).toString(),
                hourLabelWidth - 4.dp(context),
                startHeight + textHeight + 6.dp(context),
                hourLabelTextPaint
            )
            canvas.drawLine(
                hourLabelWidth,
                startHeight + (unitHeight * 0.5f),
                width.toFloat(),
                startHeight + (unitHeight * 0.5f),
                subLinePaint
            )
            startHeight += unitHeight
        }
    }


    private fun drawLecture(
        canvas: Canvas,
        lecture: LectureDto
    ) {
        for (classTime in lecture.class_time_json) {
            drawClassTime(
                canvas,
                classTime,
                classTime.place,
                lecture.course_title,
                lecture.color.bgColor ?: lecture.colorIndex.getDefaultBgColorHex(),
                lecture.color.fgColor ?: lecture.colorIndex.getDefaultFgColorHex()
            )
        }
    }

    private fun drawSelectedLecture(
        canvas: Canvas,
        selectedLecture: LectureDto
    ) {
        for (classTime in selectedLecture.class_time_json) {
            selectedLecture.color.bgRaw
            drawClassTime(
                canvas,
                classTime,
                classTime.place,
                selectedLecture.course_title,
                -0x1f1f20,
                -0xcccccd
            )
        }
    }

    private fun drawClassTime(
        canvas: Canvas,
        classTime: ClassTimeDto,
        location: String,
        courseTitle: String,
        bgColor: Int,
        fgColor: Int,
    ) {
        val dayOffset = classTime.day - trimParam.dayOfWeekFrom
        val hourRangeOffset = Pair(
            max(classTime.start - trimParam.hourFrom + 8, 0f),
            min(
                classTime.start + classTime.len - trimParam.hourFrom + 8,
                trimParam.hourTo - trimParam.hourFrom.toFloat() + 1
            )
        )


        val left = hourLabelWidth + (dayOffset) * unitWidth
        val right =
            hourLabelWidth + (dayOffset) * unitWidth + unitWidth
        val top = dayLabelHeight + (hourRangeOffset.first) * unitHeight
        val bottom =
            dayLabelHeight + (hourRangeOffset.second) * unitHeight

        val rect = RectF(left, top, right, bottom)

        canvas.drawRect(rect, Paint().apply { color = bgColor })
        canvas.drawRect(rect, Paint().apply {
            style = Paint.Style.STROKE
            color = 0x0d000000
            strokeWidth = 1.dp(context)
        })

        val cellHeight = bottom - top - cellPadding * 2
        val cellWidth = right - left - cellPadding * 2

        val courseTitleHeight = lectureCellTextRect.prepare(
            courseTitle,
            cellWidth.toInt(),
            cellHeight.toInt()
        )
        val locationHeight = lectureCellSubTextRect.prepare(
            location,
            cellWidth.toInt(),
            cellHeight.toInt() - courseTitleHeight
        )

        lectureCellTextRect.draw(
            canvas,
            (left + cellPadding).toInt(),
            (top + (cellHeight - courseTitleHeight - locationHeight) / 2).toInt(),
            cellWidth.toInt(),
            fgColor
        )

        lectureCellSubTextRect.draw(
            canvas,
            (left + cellPadding).toInt(),
            (top + courseTitleHeight + (cellHeight - courseTitleHeight - locationHeight) / 2).toInt(),
            cellWidth.toInt(),
            fgColor
        )
    }

    private fun getTextHeight(text: String, paint: Paint): Float {
        val bounds = Rect()
        paint.getTextBounds(text, 0, text.length, bounds)
        return bounds.height().toFloat()
    }
}

interface OnLectureClickListener {
    fun onClick(lecture: LectureDto)
}
