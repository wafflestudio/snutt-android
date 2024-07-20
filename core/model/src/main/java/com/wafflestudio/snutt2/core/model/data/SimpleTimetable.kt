package com.wafflestudio.snutt2.core.model.data

data class SimpleTimetable(
    val id: String,
    val courseBook: CourseBook,
    val title: String,
    val updatedAt: String,
    val totalCredit: Long,
    val isPrimary: Boolean,
)
