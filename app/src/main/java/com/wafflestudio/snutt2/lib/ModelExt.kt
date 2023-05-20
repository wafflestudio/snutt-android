package com.wafflestudio.snutt2.lib

import android.content.Context
import android.graphics.Color
import androidx.compose.runtime.Composable
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.lib.data.SNUTTStringUtils.toFormattedTimeString
import com.wafflestudio.snutt2.lib.network.dto.core.ClassTimeDto
import com.wafflestudio.snutt2.lib.network.dto.core.CourseBookDto
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.lib.network.dto.core.SimpleTableDto
import com.wafflestudio.snutt2.model.TableTrimParam
import com.wafflestudio.snutt2.model.TagType
import com.wafflestudio.snutt2.ui.SNUTTColors
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

fun CourseBookDto.toFormattedString(context: Context): String {
    val semesterStr = when (this.semester) {
        1L -> context.getString(R.string.course_book_spring_semster)
        2L -> context.getString(R.string.course_book_summer_semester)
        3L -> context.getString(R.string.course_book_authum)
        4L -> context.getString(R.string.course_book_winter)
        else -> "-"
    }
    return "${this.year} $semesterStr"
}

fun TagType.color(): Int {
    return when (this) {
        TagType.ACADEMIC_YEAR -> Color.rgb(229, 68, 89)
        TagType.CLASSIFICATION -> Color.rgb(245, 141, 61)
        TagType.CREDIT -> Color.rgb(166, 217, 48)
        TagType.DEPARTMENT -> Color.rgb(27, 208, 200)
        TagType.INSTRUCTOR -> Color.rgb(29, 153, 232)
        TagType.CATEGORY -> Color.rgb(175, 86, 179)
        TagType.ETC -> Color.rgb(0xaf, 0x56, 0xb3)
    }
}

@Composable
fun TagType.getColor(): androidx.compose.ui.graphics.Color {
    return when (this) {
        TagType.ACADEMIC_YEAR -> SNUTTColors.Red
        TagType.CLASSIFICATION -> SNUTTColors.Orange
        TagType.CREDIT -> SNUTTColors.Grass
        TagType.DEPARTMENT -> SNUTTColors.Sky
        TagType.INSTRUCTOR -> SNUTTColors.Blue
        TagType.CATEGORY -> SNUTTColors.Violet
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

// FIXME: 앞으로 index 를 가지고 color 설정하지 않는다.
fun Long.getDefaultFgColorHex(): Int {
    val DEFAULT_FG =
        listOf(-0x1, -0x1, -0x1, -0x1, -0x1, -0x1, -0x1, -0x1, -0x1, -0x1)
    return DEFAULT_FG[this.toInt()]
}

// FIXME: 앞으로 index 를 가지고 color 설정하지 않는다.
fun Long.getDefaultBgColorHex(): Int {
    val DEFAULT_BG = listOf(
        -0x1abba7,
        -0xa72c3,
        -0x53ad3,
        -0x5926d0,
        -0xd43c9a,
        -0xe42f37,
        -0xe26617,
        -0xb0b73c,
        -0x50a94d,
        -0x1f1f20
    )
    return DEFAULT_BG[this.toInt()]
}

fun LectureDto.isCourseNumberEquals(lectureDto: LectureDto): Boolean {
    return course_number != null && course_number == lectureDto.course_number
}

fun LectureDto.isLectureNumberEquals(lectureDto: LectureDto): Boolean {
    return isCourseNumberEquals(lectureDto) && lecture_number != null && lecture_number == lectureDto.lecture_number
}

fun List<LectureDto>.getFittingTrimParam(tableTrimParam: TableTrimParam): TableTrimParam =
    TableTrimParam(
        dayOfWeekFrom = (flatMap { it.class_time_json.map { it.day } } + tableTrimParam.dayOfWeekFrom).minOf { it },
        dayOfWeekTo = (flatMap { it.class_time_json.map { it.day } } + tableTrimParam.dayOfWeekTo).maxOf { it },
        hourFrom = (flatMap { it.class_time_json.map { floor(it.startTimeInFloat).toInt() } } + tableTrimParam.hourFrom).minOf { it },
        hourTo = (flatMap { it.class_time_json.map { ceil(it.endTimeInFloat).toInt() - 1 } } + tableTrimParam.hourTo).maxOf { it },
        forceFitLectures = true
    )

fun ClassTimeDto.trimByTrimParam(tableTrimParam: TableTrimParam): ClassTimeDto? {
    if (tableTrimParam.dayOfWeekFrom > this.day || this.day > tableTrimParam.dayOfWeekTo) return null
    if (tableTrimParam.hourFrom >= this.endTimeInFloat || tableTrimParam.hourTo + 1 <= this.startTimeInFloat) return null

    return this.copy(
        start_time = max(tableTrimParam.hourFrom.toFloat(), this.startTimeInFloat).toFormattedTimeString(),
        end_time = min(this.endTimeInFloat, tableTrimParam.hourTo.toFloat() + 1).toFormattedTimeString()
    )
}

fun roundToCompact(f: Float): Float {
    return if (f - f.toInt() == 0f) f
    else if (f - f.toInt() <= 0.5) f.toInt() + 0.5f
    else f.toInt() + 1f
}

fun SimpleTableDto.courseBookEquals(other: SimpleTableDto): Boolean {
    return this.semester == other.semester && this.year == other.year
}

fun SimpleTableDto.courseBookEquals(other: CourseBookDto): Boolean {
    return this.semester == other.semester && this.year == other.year
}
