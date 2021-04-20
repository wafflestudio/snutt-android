package com.wafflestudio.snutt2.view

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.*
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import com.wafflestudio.snutt2.ColorConst
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.SNUTTBaseActivity
import com.wafflestudio.snutt2.SNUTTUtils.dp2px
import com.wafflestudio.snutt2.SNUTTUtils.sp2px
import com.wafflestudio.snutt2.SNUTTUtils.zeroStr
import com.wafflestudio.snutt2.manager.LectureManager
import com.wafflestudio.snutt2.manager.PrefStorage
import com.wafflestudio.snutt2.network.dto.core.LectureDto
import com.wafflestudio.snutt2.ui.LectureMainActivity

/**
 * Created by makesource on 2016. 1. 24..
 */
// Refactoring FIXME:
// @AndroidEntryPoint 를 위젯에서 사용할 수 없어서 programmatic 하게 뷰 인플레이트 시키는 방향으로 변경
// constructor 그래서 전부 고려해서 만들지 않음 (귀찮)
// 사실 애초에 View 에 있는 도메인 로직을 바깥으로 뺄 방법 부터 생각해봐야 할 듯.
class TableView(
    context: Context,
    private val export: Boolean,
    private val lectureManager: LectureManager,
    private val prefStorage: PrefStorage
) : View(context) {

    private var backgroundPaint: Paint? = null
    private var linePaint: Paint? = null
    private var linePaint2: Paint? = null
    private var topLabelTextPaint: Paint? = null
    private var leftLabelTextPaint: Paint? = null
    private var mContext: Context = context
    private var wdays: Array<String?> = emptyArray()
    private val leftLabelWidth = context.dp2px(24.5f)
    private val topLabelHeight = context.dp2px(28.5f)
    private var unitWidth = 0f
    private var unitHeight = 0f
    private var titleTextRect: TextRect? = null
    private var locationTextRect: TextRect? = null
    private var titleTextPaint: Paint? = null
    private var locationTextPaint: Paint? = null
    private var lectures: List<LectureDto>?

    init {
        prefStorage.currentTable?.let {
            lectureManager.setLectures(it.lectureList)
        }
        lectures = lectureManager.getLectures()
        init()
    }

    // 시간표 trim 용
    private var numWidth = 0
    private var startWidth = 0
    private var numHeight = 0
    private var startHeight = 0

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        unitHeight = (height - topLabelHeight) / (numHeight * 2).toFloat()
        unitWidth = (width - leftLabelWidth) / numWidth.toFloat()
        invalidate()
    }

    private fun init() {
        isDrawingCacheEnabled = true
        setBackgroundColor(0xFFFFFFFF.toInt())
        backgroundPaint = Paint()
        backgroundPaint!!.color = -0x1
        linePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        linePaint!!.color = Color.rgb(235, 235, 235)
        linePaint!!.strokeWidth = 1f
        linePaint2 = Paint(Paint.ANTI_ALIAS_FLAG)
        linePaint2!!.color = Color.rgb(243, 243, 243)
        linePaint2!!.strokeWidth = 1f
        topLabelTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        topLabelTextPaint!!.color = Color.argb(180, 0, 0, 0)
        topLabelTextPaint!!.textSize = context.sp2px(12f)
        topLabelTextPaint!!.textAlign = Paint.Align.CENTER
        leftLabelTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        leftLabelTextPaint!!.color = Color.argb(180, 0, 0, 0)
        leftLabelTextPaint!!.textSize = context.sp2px(12f)
        leftLabelTextPaint!!.textAlign = Paint.Align.CENTER
        wdays = arrayOfNulls(7)
        wdays[0] = mContext.resources.getString(R.string.wday_mon)
        wdays[1] = mContext.resources.getString(R.string.wday_tue)
        wdays[2] = mContext.resources.getString(R.string.wday_wed)
        wdays[3] = mContext.resources.getString(R.string.wday_thu)
        wdays[4] = mContext.resources.getString(R.string.wday_fri)
        wdays[5] = mContext.resources.getString(R.string.wday_sat)
        wdays[6] = mContext.resources.getString(R.string.wday_sun)
        titleTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        titleTextPaint!!.textSize = context.sp2px(10f)
        titleTextRect = TextRect(titleTextPaint!!)
        locationTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        locationTextPaint!!.textSize = context.sp2px(11f)
        locationTextPaint!!.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        locationTextRect = TextRect(locationTextPaint!!)
        numWidth = 7
        startWidth = 0
        numHeight = 14
        startHeight = 0
    }

    fun getTextWidth(text: String, paint: Paint): Float {
        val bounds = Rect()
        paint.getTextBounds(text, 0, text.length, bounds)
        return bounds.width().toFloat()
    }

    fun getTextHeight(text: String?, paint: Paint?): Float {
        val bounds = Rect()
        paint!!.getTextBounds(text, 0, text!!.length, bounds)
        return bounds.height().toFloat()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        val wday = ((x - leftLabelWidth) / unitWidth).toInt() + startWidth
        val time = ((y - topLabelHeight) / unitHeight).toInt() / 2f + startHeight.toFloat()
        if (event.action == MotionEvent.ACTION_UP) {
            Log.d(TAG, "action up")
            Log.d(TAG, "day : $wday")
            Log.d(TAG, "time : $time")
            for (i in lectures!!.indices) {
                val lecture = lectures!![i]
                if (lectureManager.contains(lecture, wday, time)) {
                    val intent = Intent(context, LectureMainActivity::class.java)
                    intent.putExtra(SNUTTBaseActivity.INTENT_KEY_LECTURE_POSITION, i)
                    context.startActivity(intent)
                }
            }
        }
        return true
    }

    // 주어진 canvas에 시간표를 그림
    private fun drawTimetable(canvas: Canvas, canvasWidth: Int, canvasHeight: Int, export: Boolean) {
        var startWday: Int
        var endWday: Int
        var startTime: Int
        var endTime: Int
        startWday = 7
        endWday = 0
        startTime = 14
        endTime = 0
        for (lecture in lectures!!) {
            for (classTimeDto in lecture.class_time_json) {
                val wday = classTimeDto.day.toInt()
                val start = classTimeDto.start
                val duration = classTimeDto.len
                startWday = Math.min(startWday, wday)
                endWday = Math.max(endWday, wday)
                startTime = Math.min(startTime, start.toInt()) // 버림
                endTime = Math.max(endTime, (start + duration + 0.5f).toInt()) // 반올림
            }
        }
        val lec = lectureManager.getSelectedLecture()
        if (!export && lec != null) {
            for (classTimeDto in lec.class_time_json) {
                val wday = classTimeDto.day.toInt()
                val start = classTimeDto.start
                val duration = classTimeDto.len
                startWday = Math.min(startWday, wday)
                endWday = Math.max(endWday, wday)
                startTime = Math.min(startTime, start.toInt()) // 버림
                endTime = Math.max(endTime, (start + duration + 0.5f).toInt()) // 반올림
            }
        }
        if (prefStorage.autoTrim || !export) {
            // 월 : 0 , 화 : 1 , ... 금 : 4, 토 : 5
            startWidth = 0
            numWidth = Math.max(5, endWday + 1)
            //
            startHeight = Math.min(1, startTime)
            numHeight = Math.max(10, endTime - startHeight)
        } else {
            startWidth = prefStorage.trimWidthStart
            numWidth = prefStorage.trimWidthNum
            startHeight = prefStorage.trimHeightStart
            numHeight = prefStorage.trimHeightNum
        }

        // FIX: #13. Fatal Exception: java.lang.ArrayIndexOutOfBoundsException
        if (startWidth + numWidth - 1 > wdays.size - 1) {
            startWidth = 0
            numWidth = 5
            prefStorage.trimWidthStart = startWidth
            prefStorage.trimWidthNum = numWidth
        }
        unitHeight = (canvasHeight - topLabelHeight) / (numHeight * 2).toFloat()
        unitWidth = (canvasWidth - leftLabelWidth) / numWidth.toFloat()

        // 가로 줄 28개
        canvas.drawLine(0f, 0f, canvasWidth.toFloat(), 0f, linePaint!!)
        canvas.drawLine(
            0f,
            canvasHeight.toFloat(),
            canvasWidth.toFloat(),
            canvasHeight.toFloat(),
            linePaint!!
        )
        for (i in 0 until numHeight * 2) {
            val height = topLabelHeight + unitHeight * i
            if (i % 2 == 1) {
                canvas.drawLine(leftLabelWidth, height, canvasWidth.toFloat(), height, linePaint2!!)
            } else {
                canvas.drawLine(
                    leftLabelWidth / 3f,
                    height,
                    canvasWidth.toFloat(),
                    height,
                    linePaint!!
                )
            }
        }
        // 세로 줄 그리기
        for (i in 0 until numWidth) {
            val width = leftLabelWidth + unitWidth * i
            val textHeight = getTextHeight(wdays[0], topLabelTextPaint)
            canvas.drawLine(width, 0f, width, canvasHeight.toFloat(), linePaint!!)
            canvas.drawText(
                wdays[i + startWidth]!!,
                leftLabelWidth + unitWidth * (i + 0.5f),
                (topLabelHeight + textHeight) / 2f,
                topLabelTextPaint!!
            )
        }
        canvas.drawLine(0f, 0f, 0f, canvasHeight.toFloat(), linePaint!!)
        canvas.drawLine(
            canvasWidth.toFloat(),
            0f,
            canvasWidth.toFloat(),
            canvasHeight.toFloat(),
            linePaint!!
        )
        // 교시 텍스트 그리기
        for (i in 0 until numHeight) {
            val str1: String = (i + startHeight).toString() + "교시"
            val str2 = zeroStr(i + startHeight + 8) + ":00~" + zeroStr(i + startHeight + 9) + ":00"
            val str = (i + startHeight + 8).toString()
            // float textHeight = getTextHeight(str1, leftLabelTextPaint);
            // float textHeight2 = getTextHeight(str2, leftLabelTextPaint);
            // float padding = SNUTTApplication.dpTopx(5);
            // if (canvasWidth > canvasHeight) padding = 0;
            // float height = topLabelHeight + unitHeight * (i * 2 + 1) + (textHeight + textHeight2 + padding) / 2f;
            // canvas.drawText(str1, leftLabelWidth/2f, height - textHeight2 - padding, leftLabelTextPaint);
            // canvas.drawText(str2, leftLabelWidth/2f, height, leftLabelTextPaint);
            val padding = context.dp2px(5f)
            canvas.drawText(
                str,
                leftLabelWidth / 2f,
                topLabelHeight + unitHeight * (i * 2) + unitHeight / 2f + padding,
                leftLabelTextPaint!!
            )
        }
        // 내 강의 그리기
        if (lectures != null) {
            for (i in lectures!!.indices) {
                val lecture = lectures!![i]
                if (lecture.colorIndex == 0L) drawLecture(
                    canvas,
                    canvasWidth.toFloat(),
                    canvasHeight.toFloat(),
                    lecture,
                    lecture.color.bgColor,
                    lecture.color.fgColor
                ) else drawLecture(
                    canvas,
                    canvasWidth.toFloat(),
                    canvasHeight.toFloat(),
                    lecture,
                    lecture.colorIndex.toInt()
                )
            }
        }
        if (!export) {
            // 현재 선택한 강의 그리기
            val selectedLecture = lectureManager.getSelectedLecture()
            if (selectedLecture != null && !lectureManager.alreadyOwned(selectedLecture)) {
                drawLecture(
                    canvas,
                    canvasWidth.toFloat(),
                    canvasHeight.toFloat(),
                    selectedLecture,
                    ColorConst.defaultBgColor,
                    ColorConst.defaultFgColor
                )
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        drawTimetable(canvas, width, height, export)
    }

    fun drawWidget(canvas: Canvas, width: Int, height: Int) {
        drawTimetable(canvas, width, height, true)
    }

    private fun drawLecture(
        canvas: Canvas,
        canvasWidth: Float,
        canvasHeight: Float,
        lecture: LectureDto,
        bgColor: Int,
        fgColor: Int
    ) {
        // class_time : 수(6-2) -> {"day":2,"start":6,"len":2,"place":"301-118","_id":"569f967697f670df460ed3d8"}
        for (classTimeDto in lecture.class_time_json) {
            val wday = classTimeDto.day.toInt()
            val startTime = classTimeDto.start
            val duration = classTimeDto.len
            val location = classTimeDto.place
            drawClass(canvas, canvasWidth, canvasHeight, lecture.course_title, location, wday, startTime, duration, bgColor, fgColor)
        }
    }

    private fun drawLecture(
        canvas: Canvas,
        canvasWidth: Float,
        canvasHeight: Float,
        lecture: LectureDto,
        colorIndex: Int
    ) {
        // class_time : 수(6-2) -> {"day":2,"start":6,"len":2,"place":"301-118","_id":"569f967697f670df460ed3d8"}
        for (classTimeDto in lecture.class_time_json) {
            val wday = classTimeDto.day
            val startTime = classTimeDto.start
            val duration = classTimeDto.len
            val location = classTimeDto.place
            val bgColor = lectureManager.getBgColorByIndex(colorIndex)
            val fgColor = lectureManager.getFgColorByIndex(colorIndex)
            drawClass(canvas, canvasWidth, canvasHeight, lecture.course_title, location, wday, startTime, duration, bgColor, fgColor)
        }
    }

    // 사각형 하나를 그림
    private fun drawClass(
        canvas: Canvas,
        canvasWidth: Float,
        canvasHeight: Float,
        course_title: String?,
        location: String,
        wday: Int,
        startTime: Float,
        duration: Float,
        bgColor: Int,
        fgColor: Int
    ) {
        val unitHeight = (canvasHeight - topLabelHeight) / (numHeight * 2).toFloat()
        val unitWidth = (canvasWidth - leftLabelWidth) / numWidth.toFloat()
        if (wday - startWidth < 0) return // 날자가 잘리는 경우
        if ((startTime - startHeight) * unitHeight * 2 + unitHeight * duration * 2 < 0) return // 교시가 잘리는 경우

        // startTime : 시작 교시
        val left = leftLabelWidth + (wday - startWidth) * unitWidth
        val right = leftLabelWidth + (wday - startWidth) * unitWidth + unitWidth
        val top = topLabelHeight + Math.max(0f, startTime - startHeight) * unitHeight * 2
        val bottom = topLabelHeight + (startTime - startHeight) * unitHeight * 2 + unitHeight * duration * 2
        val borderWidth = context.dp2px(3f)
        val r = RectF(left, top, right, bottom)
        val p = Paint()
        p.color = bgColor
        canvas.drawRect(r, p)
        val s = Paint()
        s.style = Paint.Style.STROKE
        s.color = 0x0d000000
        s.strokeWidth = 2f
        canvas.drawRect(r, s)

        // 강의명, 강의실 기록
        val padding = 5
        val width = (right - left).toInt() - padding * 2
        val height = (bottom - top).toInt() - padding * 2
        val str1Height = titleTextRect!!.prepare(course_title!!, width, height)
        val str2Height = locationTextRect!!.prepare(location, width, height - str1Height)
        titleTextRect!!.draw(
            canvas,
            left.toInt() + padding,
            (top + (height - str1Height - str2Height) / 2).toInt() + padding,
            width,
            fgColor
        )
        locationTextRect!!.draw(
            canvas,
            left.toInt() + padding,
            (top + str1Height + (height - str1Height - str2Height) / 2).toInt() + padding,
            width,
            fgColor
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        val dm = mContext.resources.displayMetrics
        val w = dm.widthPixels
        val h = dm.heightPixels
        val desiredHeight = h - tabBarHeight - statusBarHeight - actionBarHeight
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val width: Int
        val height: Int

        // Measure Width
        width = if (widthMode == MeasureSpec.EXACTLY) {
            // Must be this size
            widthSize
        } else if (widthMode == MeasureSpec.AT_MOST) {
            // Can't be bigger than...
            Math.min(w, widthSize)
        } else {
            // Be whatever you want
            w
        }

        // Measure Height
        height = if (heightMode == MeasureSpec.EXACTLY) {
            // Must be this size
            heightSize
        } else if (heightMode == MeasureSpec.AT_MOST) {
            // Can't be bigger than...
            Math.min(desiredHeight, heightSize)
        } else {
            // Be whatever you want
            desiredHeight
        }
        // MUST CALL THIS
        setMeasuredDimension(width, height)
    }

    private val statusBarHeight: Int
        private get() {
            var statusHeight = 0
            val screenSizeType = mContext.resources.configuration.screenLayout and
                Configuration.SCREENLAYOUT_SIZE_MASK
            if (screenSizeType != Configuration.SCREENLAYOUT_SIZE_XLARGE) {
                val resourceId = mContext.resources.getIdentifier(
                    "status_bar_height",
                    "dimen",
                    "android"
                )
                if (resourceId > 0) {
                    statusHeight = mContext.resources.getDimensionPixelSize(resourceId)
                }
            }
            return statusHeight
        }
    private val actionBarHeight: Int
        private get() {
            val tv = TypedValue()
            mContext.theme.resolveAttribute(R.attr.actionBarSize, tv, true)
            return resources.getDimensionPixelSize(tv.resourceId)
        }
    private val tabBarHeight: Int
        private get() {
            var tabBarHeight = 0
            tabBarHeight = mContext.resources.getDimensionPixelSize(R.dimen.tab_bar_height)
            return tabBarHeight
        }

    companion object {
        private const val TAG = "VIEW_TAG_TABLE_VIEW"
    }
}
