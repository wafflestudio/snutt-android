package com.wafflestudio.snutt2.ui.util

import android.content.Context
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.domain.model.CourseBook

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
