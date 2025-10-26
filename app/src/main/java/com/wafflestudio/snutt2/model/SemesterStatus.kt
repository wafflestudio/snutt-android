package com.wafflestudio.snutt2.model

import com.wafflestudio.snutt2.domainmodel.CourseBook

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
