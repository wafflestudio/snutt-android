package com.wafflestudio.snutt2.core.model.data.timetable

import com.wafflestudio.snutt2.core.model.data.CourseBook

data class SimpleTimeTable(
    val id: String,
    val courseBook: CourseBook,
    val title: String,
    val totalCredit: Long,
    val isPrimary: Boolean,
)