package com.wafflestudio.snutt2.components.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.data.SNUTTStorage
import com.wafflestudio.snutt2.domainmodel.CustomLecture
import com.wafflestudio.snutt2.domainmodel.LectureColor
import com.wafflestudio.snutt2.domainmodel.LectureSession
import com.wafflestudio.snutt2.domainmodel.LocalLecture
import com.wafflestudio.snutt2.domainmodel.TableTheme
import com.wafflestudio.snutt2.domainmodel.TableTrimParam
import com.wafflestudio.snutt2.lib.contains
import com.wafflestudio.snutt2.lib.dp
import com.wafflestudio.snutt2.lib.getFittingTrimParam
import com.wafflestudio.snutt2.lib.roundToCompact
import com.wafflestudio.snutt2.lib.sp
import com.wafflestudio.snutt2.lib.toDayString
import com.wafflestudio.snutt2.ui.isSystemDarkMode
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
            strokeWidth = (0.5f).dp(context)
        }

    private val subLinePaint: Paint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(243, 243, 243)
            strokeWidth = (0.5f).dp(context)
        }

    private val hourLabelTextPaint: Paint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(180, 0, 0, 0)
            textSize = 12.sp(context)
            textAlign = Paint.Align.RIGHT
            typeface = ResourcesCompat.getFont(context, R.font.pretendard_light)
        }

    private val dayLabelTextPaint: Paint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(180, 0, 0, 0)
            textSize = 12.sp(context)
            textAlign = Paint.Align.CENTER
            typeface = ResourcesCompat.getFont(context, R.font.pretendard_light)
        }

    private val lectureCellTextRect =
        TextRect(
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                textSize = 10f.sp(context)
                typeface = ResourcesCompat.getFont(context, R.font.pretendard_regular)
            },
        )

    private val lectureCellSubTextRect =
        TextRect(
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                textSize = 11f.sp(context)
                typeface = ResourcesCompat.getFont(context, R.font.pretendard_bold)
            },
        )

    var trimParam: TableTrimParam = TableTrimParam.Default
        set(value) {
            field = value
            invalidateTrimParam()
            invalidate()
        }

    private var fittedTrimParam: TableTrimParam = TableTrimParam.Default

    var lectures: List<LocalLecture> = listOf()
        set(value) {
            field = value
            invalidateTrimParam()
            invalidate()
        }

    var selectedLecture: LocalLecture? = null
        set(value) {
            field = value
            invalidateTrimParam()
            invalidate()
        }

    var theme: TableTheme? = null
        set(value) {
            field = value
            invalidate()
        }

    private val unitWidth: Float
        get() = (width - hourLabelWidth) / (fittedTrimParam.dayOfWeekTo - fittedTrimParam.dayOfWeekFrom + 1)

    private val unitHeight: Float
        get() = (height - dayLabelHeight) / (fittedTrimParam.hourTo - fittedTrimParam.hourFrom + 1)

    private var isDarkMode: Boolean = false
    private var compactMode: Boolean = false

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, compactMode: Boolean) : super(context) {
        init()
        this.compactMode = compactMode
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle,
    ) {
        init()
    }

    private fun init() {
        val sharedPreferences = context.getSharedPreferences(
            SNUTTStorage.DOMAIN_SCOPE_CURRENT_VERSION,
            Context.MODE_PRIVATE,
        )
        val themeMode = sharedPreferences.getString("theme_mode", null) ?: ""
        isDarkMode = when (themeMode) {
            "\"DARK\"" -> true
            "\"LIGHT\"" -> false
            else -> isSystemDarkMode(context)
        }

        setBackgroundColor(
            if (isDarkMode) Color.rgb(43, 43, 43) else Color.rgb(255, 255, 255),
        )
        linePaint.apply {
            color = if (isDarkMode) Color.rgb(60, 60, 60) else Color.rgb(235, 235, 235)
        }
        subLinePaint.apply {
            color = if (isDarkMode) Color.rgb(60, 60, 60) else Color.rgb(243, 243, 243)
        }
        hourLabelTextPaint.apply {
            color = if (isDarkMode) Color.rgb(119, 119, 119) else Color.rgb(0, 0, 0)
        }
        dayLabelTextPaint.apply {
            color = if (isDarkMode) Color.rgb(119, 119, 119) else Color.rgb(0, 0, 0)
        }
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

    private fun setOnLectureClickListener(listener: (lecture: LocalLecture) -> Unit) {
        this.onLectureClickListener = object : OnLectureClickListener {
            override fun onClick(lecture: LocalLecture) {
                listener(lecture)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        val day = ((x - hourLabelWidth) / unitWidth).toInt() + fittedTrimParam.dayOfWeekFrom
        val time = ((y - dayLabelHeight) / unitHeight) + fittedTrimParam.hourFrom - 8

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

        val verticalLines = fittedTrimParam.dayOfWeekTo - fittedTrimParam.dayOfWeekFrom + 1
        var startWidth = hourLabelWidth
        repeat(verticalLines) {
            canvas.drawLine(startWidth, 0f, startWidth, height.toFloat(), linePaint)
            canvas.drawText(
                (fittedTrimParam.dayOfWeekFrom + it).toDayString(context),
                startWidth + unitWidth * 0.5f,
                (dayLabelHeight + textHeight) / 2f,
                dayLabelTextPaint,
            )
            startWidth += unitWidth
        }

        val horizontalLines = fittedTrimParam.hourTo - fittedTrimParam.hourFrom + 1
        var startHeight = dayLabelHeight
        repeat(horizontalLines) {
            canvas.drawLine(0f, startHeight, width.toFloat(), startHeight, linePaint)
            canvas.drawText(
                (fittedTrimParam.hourFrom + it).toString(),
                hourLabelWidth - 4.dp(context),
                startHeight + textHeight + 6.dp(context),
                hourLabelTextPaint,
            )
            canvas.drawLine(
                hourLabelWidth,
                startHeight + (unitHeight * 0.5f),
                width.toFloat(),
                startHeight + (unitHeight * 0.5f),
                subLinePaint,
            )
            startHeight += unitHeight
        }
    }

    private fun drawLecture(
        canvas: Canvas,
        lecture: LocalLecture,
    ) {
        val (fgColor, bgColor) = when (val color = lecture.color) {
            is LectureColor.Custom -> color.foreground to color.background
            is LectureColor.BuiltIn -> {
                val palette = theme?.getColors(isDarkMode) ?: return
                val entry = palette[color.colorIndex.coerceIn(palette.indices)]
                entry.foreground to entry.background
            }
        }

        for (session in lecture.lectureSessions) {
            drawClassTime(
                canvas,
                session,
                session.place,
                lecture.courseTitle,
                bgColor,
                fgColor,
                isCustom = lecture is CustomLecture,
            )
        }
    }

    private fun drawSelectedLecture(
        canvas: Canvas,
        selectedLecture: LocalLecture,
    ) {
        for (session in selectedLecture.lectureSessions) {
            drawClassTime(
                canvas,
                session,
                session.place,
                selectedLecture.courseTitle,
                -0x1f1f20,
                -0xcccccd,
            )
        }
    }

    private fun drawClassTime(
        canvas: Canvas,
        session: LectureSession,
        location: String,
        courseTitle: String,
        bgColor: Int,
        fgColor: Int,
        isCustom: Boolean = false,
    ) {
        val dayValue = session.day.value - 1
        val startTime = session.startTime.hour + session.startTime.minute / 60f
        val endTime = session.endTime.hour + session.endTime.minute / 60f

        val dayOffset = dayValue - fittedTrimParam.dayOfWeekFrom
        val hourRangeOffset = Pair(
            max(startTime - fittedTrimParam.hourFrom, 0f),
            min(
                endTime.let { if (isCustom.not() && compactMode) roundToCompact(it) else it } - fittedTrimParam.hourFrom,
                fittedTrimParam.hourTo - fittedTrimParam.hourFrom.toFloat() + 1,
            ),
        )

        val left = hourLabelWidth + (dayOffset) * unitWidth
        val right =
            hourLabelWidth + (dayOffset) * unitWidth + unitWidth
        val top = dayLabelHeight + (hourRangeOffset.first) * unitHeight
        val bottom =
            dayLabelHeight + (hourRangeOffset.second) * unitHeight

        val rect = RectF(left, top, right, bottom)

        canvas.drawRect(rect, Paint().apply { color = bgColor })
        canvas.drawRect(
            rect,
            Paint().apply {
                style = Paint.Style.STROKE
                color = 0x0d000000
                strokeWidth = 1.dp(context)
            },
        )

        val cellHeight = bottom - top - cellPadding * 2
        val cellWidth = right - left - cellPadding * 2

        val courseTitleHeight = lectureCellTextRect.prepare(
            courseTitle,
            cellWidth.toInt(),
            cellHeight.toInt(),
        )
        val locationHeight = lectureCellSubTextRect.prepare(
            location,
            cellWidth.toInt(),
            cellHeight.toInt() - courseTitleHeight,
        )

        lectureCellTextRect.draw(
            canvas,
            (left + cellPadding).toInt(),
            (top + (cellHeight - courseTitleHeight - locationHeight) / 2).toInt(),
            cellWidth.toInt(),
            fgColor,
        )

        lectureCellSubTextRect.draw(
            canvas,
            (left + cellPadding).toInt(),
            (top + courseTitleHeight + (cellHeight - courseTitleHeight - locationHeight) / 2).toInt(),
            cellWidth.toInt(),
            fgColor,
        )
    }

    private fun getTextHeight(text: String, paint: Paint): Float {
        val bounds = Rect()
        paint.getTextBounds(text, 0, text.length, bounds)
        return bounds.height().toFloat()
    }

    private fun invalidateTrimParam() {
        fittedTrimParam = if (trimParam.forceFitLectures) {
            (
                selectedLecture?.let {
                    lectures + it
                } ?: lectures
                ).getFittingTrimParam(TableTrimParam.Default)
        } else {
            trimParam
        }
    }
}

interface OnLectureClickListener {
    fun onClick(lecture: LocalLecture)
}
