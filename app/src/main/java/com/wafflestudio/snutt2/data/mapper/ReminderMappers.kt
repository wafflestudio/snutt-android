package com.wafflestudio.snutt2.data.mapper

import com.wafflestudio.snutt2.domainmodel.LectureReminderOffset
import com.wafflestudio.snutt2.domainmodel.LectureWithReminderOption
import com.wafflestudio.snutt2.network.dto.TimetableLectureReminderDto

fun TimetableLectureReminderDto.toDomain(): LectureWithReminderOption = LectureWithReminderOption(
    lectureId = lectureId,
    lectureTitle = courseTitle,
    lectureReminderOffset = when (offsetMinutes) {
        "TEN_MINUTES_BEFORE" -> LectureReminderOffset.TEN_MINUTES_BEFORE
        "ZERO_MINUTE" -> LectureReminderOffset.AT_START_TIME
        "TEN_MINUTES_AFTER" -> LectureReminderOffset.TEN_MINUTES_AFTER
        else -> LectureReminderOffset.NONE
    },
)
