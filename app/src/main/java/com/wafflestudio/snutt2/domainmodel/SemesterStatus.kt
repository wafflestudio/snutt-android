package com.wafflestudio.snutt2.domainmodel

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SemesterStatus(
    val current: CourseBook?,
    val next: CourseBook,
) {
    fun isActiveSemester(courseBook: CourseBook): Boolean {
        return courseBook == current || (current == null && courseBook == next)
    }

    companion object {
        val Default = SemesterStatus(
            current = null,
            next = CourseBook(semester = 0L, year = 0L),
        )
    }
}
