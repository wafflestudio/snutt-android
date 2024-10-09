package com.wafflestudio.snutt2.lib.network.dto.core

import android.content.Context
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.R

@JsonClass(generateAdapter = true)
data class CourseBookDto(
    @Json(name = "semester") val semester: Long,
    @Json(name = "year") val year: Long,
) : Comparable<CourseBookDto> {
    override fun compareTo(other: CourseBookDto): Int {
        if (year > other.year) {
            return -1
        } else if (year < other.year) {
            return 1
        } else {
            if (semester > other.semester) {
                return -1
            } else if (semester < other.semester) return 1
        }
        return 0
    }
}

fun CourseBookDto.toFullString(context: Context): String {
    return StringBuilder()
        .append(this.year)
        .append("년 ")
        .append(
            when (this.semester) {
                1L -> context.getString(R.string.course_book_spring_semster)
                2L -> context.getString(R.string.course_book_summer_semester)
                3L -> context.getString(R.string.course_book_authum)
                4L -> context.getString(R.string.course_book_winter)
                else -> ""
            },
        )
        .toString()
}
