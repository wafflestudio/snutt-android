package com.wafflestudio.snutt2.model

import com.wafflestudio.snutt2.domainmodel.CourseBook

data class SemesterStatus(
    val current: CourseBook?,
    val next: CourseBook,
)
