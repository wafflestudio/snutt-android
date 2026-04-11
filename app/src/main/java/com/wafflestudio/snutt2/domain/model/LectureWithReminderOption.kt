package com.wafflestudio.snutt2.domain.model

data class LectureWithReminderOption(
    val lectureId: String,
    val lectureTitle: String,
    val lectureReminderOffset: LectureReminderOffset,
) {
    companion object {
        val Default = LectureWithReminderOption(
            lectureId = "",
            lectureTitle = "",
            lectureReminderOffset = LectureReminderOffset.NONE,
        )
    }
}
