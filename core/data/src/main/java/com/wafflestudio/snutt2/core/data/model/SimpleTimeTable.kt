package com.wafflestudio.snutt2.core.data.model

import com.wafflestudio.snutt2.core.model.data.CourseBook
import com.wafflestudio.snutt2.core.model.data.timetable.SimpleTimeTable
import com.wafflestudio.snutt2.core.network.model.SimpleTableDto

fun SimpleTableDto.toExternalModel() = SimpleTimeTable(
    id = this.id,
    courseBook = CourseBook(
        semester = this.semester,
        year = this.year,
    ),
    title = this.title,
    totalCredit = this.totalCredit ?: 0L,
    isPrimary = this.isPrimary,
)