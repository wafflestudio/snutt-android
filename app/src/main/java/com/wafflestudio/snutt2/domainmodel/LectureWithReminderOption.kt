package com.wafflestudio.snutt2.domainmodel

import com.wafflestudio.snutt2.views.logged_in.home.settings.LectureReminderOffset

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
