package com.wafflestudio.snutt2.lib

import android.content.Context
import android.util.Log
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.lib.network.dto.core.CourseBookDto
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.ui.TableListActivity


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
