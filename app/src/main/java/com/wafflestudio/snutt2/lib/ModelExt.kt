package com.wafflestudio.snutt2.lib

import android.content.Context
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.domainmodel.CourseBook
import com.wafflestudio.snutt2.domainmodel.Lecture
import com.wafflestudio.snutt2.domainmodel.LectureReviewInfo
import com.wafflestudio.snutt2.domainmodel.LectureSession
import com.wafflestudio.snutt2.domainmodel.TableTrimParam
import com.wafflestudio.snutt2.network.dto.LectureDto
import com.wafflestudio.snutt2.domainmodel.TagType
import com.wafflestudio.snutt2.ui.SNUTTColors
import java.time.LocalTime
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

fun LectureDto.contains(queryDay: Int, queryTime: Float): Boolean {
    for (classTimeDto in this.class_time_json) {
        val start = classTimeDto.startTimeInFloat
        val end = classTimeDto.endTimeInFloat

        if (queryDay != classTimeDto.day) continue
        if (queryTime in start..end) return true
    }
    return false
}

fun CourseBook.toFormattedString(context: Context): String {
    val semesterStr = when (this.semester) {
        1L -> context.getString(R.string.course_book_spring_semster)
        2L -> context.getString(R.string.course_book_summer_semester)
        3L -> context.getString(R.string.course_book_authum)
        4L -> context.getString(R.string.course_book_winter)
        else -> "-"
    }
    return context.getString(R.string.course_book_year_semester_format, this.year, semesterStr)
}

fun CourseBook.toAbbvString(context: Context): String {
    val semesterStr = when (this.semester) {
        1L -> "1"
        2L -> context.getString(R.string.course_book_abbv_summer)
        3L -> "2"
        4L -> context.getString(R.string.course_book_abbv_winter)
        else -> ""
    }
    return "${this.year.rem(2000)}-$semesterStr"
}

fun TagType.color(): androidx.compose.ui.graphics.Color {
    return when (this) {
        TagType.SORT_CRITERIA -> SNUTTColors.Gray30
        TagType.CLASSIFICATION -> SNUTTColors.Red
        TagType.DEPARTMENT -> SNUTTColors.Orange
        TagType.ACADEMIC_YEAR -> SNUTTColors.Grass
        TagType.CREDIT -> SNUTTColors.Sky
        TagType.TIME -> SNUTTColors.Blue
        TagType.CATEGORY -> SNUTTColors.NavyBlue
        TagType.CATEGORY_PRE2025 -> SNUTTColors.NavyBlue
        TagType.ETC -> SNUTTColors.Violet
    }
}

fun <T> concatenate(vararg lists: List<T>): List<T> {
    val result: MutableList<T> = ArrayList()
    for (list in lists) {
        result += list
    }
    return result
}

// FIXME: 서버 인터페이스 수정 필요, 클라단에서 불필요한 컨버팅으로 보임
fun String.toCreditNumber(): Long {
    return substring(0, length - 2).toLong()
}

@JvmName("getFittingTrimParamDto")
fun List<LectureDto>.getFittingTrimParam(tableTrimParam: TableTrimParam): TableTrimParam =
    TableTrimParam(
        dayOfWeekFrom = (flatMap { it.class_time_json.map { it.day } } + tableTrimParam.dayOfWeekFrom).minOf { it },
        dayOfWeekTo = (flatMap { it.class_time_json.map { it.day } } + tableTrimParam.dayOfWeekTo).maxOf { it },
        hourFrom = (flatMap { it.class_time_json.map { floor(it.startTimeInFloat).toInt() } } + tableTrimParam.hourFrom).minOf { it },
        hourTo = (flatMap { it.class_time_json.map { ceil(it.endTimeInFloat).toInt() - 1 } } + tableTrimParam.hourTo).maxOf { it },
        forceFitLectures = true,
    )

val LectureSession.startTimeInFloat: Float
    get() = startTime.hour + startTime.minute / 60f

val LectureSession.endTimeInFloat: Float
    get() = endTime.hour + endTime.minute / 60f

fun LectureSession.trimByTrimParam(tableTrimParam: TableTrimParam): LectureSession? {
    val dayIdx = day.ordinal
    if (tableTrimParam.dayOfWeekFrom > dayIdx || dayIdx > tableTrimParam.dayOfWeekTo) return null
    if (tableTrimParam.hourFrom >= endTimeInFloat || tableTrimParam.hourTo + 1 <= startTimeInFloat) return null

    val clampedStartMinutes = max(tableTrimParam.hourFrom * 60, startTime.hour * 60 + startTime.minute)
    val clampedEndMinutes = min(endTime.hour * 60 + endTime.minute, (tableTrimParam.hourTo + 1) * 60)

    return this.copy(
        startTime = LocalTime.of(clampedStartMinutes / 60, clampedStartMinutes % 60),
        endTime = LocalTime.of(clampedEndMinutes / 60, clampedEndMinutes % 60),
    )
}

@JvmName("getFittingTrimParamForLecture")
fun List<Lecture>.getFittingTrimParam(tableTrimParam: TableTrimParam): TableTrimParam =
    TableTrimParam(
        dayOfWeekFrom = (flatMap { it.lectureSessions.map { it.day.ordinal } } + tableTrimParam.dayOfWeekFrom).minOf { it },
        dayOfWeekTo = (flatMap { it.lectureSessions.map { it.day.ordinal } } + tableTrimParam.dayOfWeekTo).maxOf { it },
        hourFrom = (flatMap { it.lectureSessions.map { floor(it.startTimeInFloat).toInt() } } + tableTrimParam.hourFrom).minOf { it },
        hourTo = (flatMap { it.lectureSessions.map { ceil(it.endTimeInFloat).toInt() - 1 } } + tableTrimParam.hourTo).maxOf { it },
        forceFitLectures = true,
    )

fun Lecture.contains(queryDay: Int, queryTime: Float): Boolean {
    for (session in this.lectureSessions) {
        if (queryDay != session.day.ordinal) continue
        if (queryTime in session.startTimeInFloat..session.endTimeInFloat) return true
    }
    return false
}

fun roundToCompact(f: Float): Float {
    return if (f - f.toInt() == 0f) {
        f
    } else if (f - f.toInt() <= 0.5) {
        f.toInt() + 0.5f
    } else {
        f.toInt() + 1f
    }
}

fun LectureReviewInfo.getReviewUrl(context: Context): String? {
    return id.let {
        context.getString(R.string.review_base_url) + "/detail?id=$it"
    }
}
