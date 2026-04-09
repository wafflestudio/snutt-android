package com.wafflestudio.snutt2.domain.model

data class TimetableLectureReminders(
    val timetableId: String,
    val lectureReminders: List<LectureWithReminderOption>,
)
