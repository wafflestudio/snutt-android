package com.wafflestudio.snutt2.core.data.model

import com.wafflestudio.snutt2.core.model.data.CourseBook
import com.wafflestudio.snutt2.core.model.data.theme.TableTheme
import com.wafflestudio.snutt2.core.model.data.timetable.TimeTable
import com.wafflestudio.snutt2.core.network.model.TableDto

fun TableDto.toExternalModel() = TimeTable(
    id = this.id,
    courseBook = CourseBook(
        semester = this.semester,
        year = this.year,
    ),
    title = this.title,
    lectureList = this.lectureList.map { it.toTimetableLecture() },
    totalCredit = this.totalCredit ?: 0L,
    theme = this.theme
    isPrimary = this.isPrimary,
)