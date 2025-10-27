package com.wafflestudio.snutt2.model

import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.domainmodel.CourseBook

@JsonClass(generateAdapter = true)
data class SemesterStatus(
    val current: CourseBook?,
    val next: CourseBook,
) {
    companion object {
        val Default = SemesterStatus(
            current = null,
            next = CourseBook(semester = 0L, year = 0L),
        )
    }
}
