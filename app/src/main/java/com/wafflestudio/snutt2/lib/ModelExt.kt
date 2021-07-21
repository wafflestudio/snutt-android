package com.wafflestudio.snutt2.lib

import android.content.Context
import android.graphics.Color
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.lib.network.dto.core.CourseBookDto
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.model.TagType


fun LectureDto.contains(queryDay: Int, queryTime: Float): Boolean {
    for (classTimeDto in this.class_time_json) {
        val day1 = classTimeDto.day
        val start1 = classTimeDto.start
        val len1 = classTimeDto.len
        val end1 = start1 + len1
        val len2 = 0.5f
        val end2 = queryTime + len2
        if (day1 != queryDay) continue
        if (!(end1 <= queryTime || end2 <= start1)) return true
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
        listOf(-0x1, -0x1, -0x1, -0x1, -0x1, -0x1, -0x1, -0x1, -0x1, -0xcccccd)
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

fun LectureDto.isRegularlyEquals(lectureDto: LectureDto): Boolean {
    return course_number != null && course_number == lectureDto.course_number

}
