package com.wafflestudio.snutt2.core.data.model

import com.wafflestudio.snutt2.core.model.data.CourseBook
import com.wafflestudio.snutt2.core.model.data.timetable.SimpleTimeTable
import com.wafflestudio.snutt2.core.network.model.SimpleTableDto

fun SimpleTableDto.toExternalModel() = SimpleTimeTable(
    id = id,
    courseBook = CourseBook(
        semester = semester,
        year = year,
    ),
    title = title,
    totalCredit = totalCredit ?: 0L,
    isPrimary = isPrimary,
)