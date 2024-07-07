package com.wafflestudio.snutt2.core.model.data.timetable

import com.wafflestudio.snutt2.core.model.data.CourseBook
import com.wafflestudio.snutt2.core.model.data.lecture.TimetableLecture
import com.wafflestudio.snutt2.core.model.data.theme.TableTheme

data class TimeTable(
    val id: String,
    val courseBook: CourseBook,
    val title: String,
    val lectureList: List<TimetableLecture>,
    val totalCredit: Long,
    val theme: TableTheme,
    val isPrimary: Boolean,
)