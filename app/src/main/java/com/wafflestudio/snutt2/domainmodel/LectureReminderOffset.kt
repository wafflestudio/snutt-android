package com.wafflestudio.snutt2.domainmodel

enum class LectureReminderOffset {
    NONE, TEN_MINUTES_BEFORE, AT_START_TIME, TEN_MINUTES_AFTER
}

fun LectureReminderOffset.toOffsetString(): String = when (this) {
    LectureReminderOffset.TEN_MINUTES_BEFORE -> "TEN_MINUTES_BEFORE"
    LectureReminderOffset.AT_START_TIME -> "ZERO_MINUTE"
    LectureReminderOffset.TEN_MINUTES_AFTER -> "TEN_MINUTES_AFTER"
    else -> "NONE"
}
