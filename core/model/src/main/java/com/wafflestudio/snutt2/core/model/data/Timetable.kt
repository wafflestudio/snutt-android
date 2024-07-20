package com.wafflestudio.snutt2.core.model.data

import com.wafflestudio.snutt2.core.model.data.lecture.TimetableLecture

data class Timetable(
    val id: String,
    val courseBook: CourseBook,
    val title: String,
    val lectureList: List<TimetableLecture>,
    val updateAt: String,
    val totalCredit: Long,
    val isPrimary: Boolean,
    val theme: Theme,
)