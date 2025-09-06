package com.wafflestudio.snutt2.domainmodel

enum class LectureReminderOffset {
    NONE, TEN_MINUTES_BEFORE, AT_START_TIME, TEN_MINUTES_AFTER
}

fun LectureReminderOffset.getIntOffset(): Int = when (this) {
    LectureReminderOffset.TEN_MINUTES_BEFORE -> -10
    LectureReminderOffset.TEN_MINUTES_AFTER -> 10
    else -> 0
}
