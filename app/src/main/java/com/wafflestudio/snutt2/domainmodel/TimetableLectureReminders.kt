package com.wafflestudio.snutt2.domainmodel

data class TimetableLectureReminders(
    val timetableId: String,
    val lectureReminders: List<LectureWithReminderOption>,
)
