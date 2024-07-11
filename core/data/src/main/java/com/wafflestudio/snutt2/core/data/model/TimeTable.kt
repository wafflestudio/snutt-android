package com.wafflestudio.snutt2.core.data.model

import com.wafflestudio.snutt2.core.model.data.CourseBook
import com.wafflestudio.snutt2.core.model.data.theme.TableTheme
import com.wafflestudio.snutt2.core.model.data.timetable.TimeTable
import com.wafflestudio.snutt2.core.network.model.TableDto

fun TableDto.toExternalModel() = TimeTable(
    id = id,
    courseBook = CourseBook(
        semester = semester,
        year = year,
    ),
    title = title,
    lectureList = lectureList.map { it.toTimetableLecture() },
    totalCredit = totalCredit ?: 0L,
    theme = theme,
    isPrimary = isPrimary,
)