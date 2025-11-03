package com.wafflestudio.snutt2.lib.network.dto.core

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.domainmodel.LectureReminderOffset
import com.wafflestudio.snutt2.domainmodel.LectureWithReminderOption

// NOTE: 강의 리마인더가 지정된 적 없는 강의의 경우 empty response가 오는데 그 경우 EOFException이 나게 되고, ExceptionMapper에서 DomainError.EOF로 변환한다.
@JsonClass(generateAdapter = true)
data class TimetableLectureReminderDto(
    @param:Json(name = "timetableLectureId") val lectureId: String,
    @param:Json(name = "courseTitle") val courseTitle: String,
    @param:Json(name = "option") val offsetMinutes: String,
)

fun TimetableLectureReminderDto.toDomainModel(): LectureWithReminderOption =
    LectureWithReminderOption(
        lectureId = lectureId,
        lectureTitle = courseTitle,
        lectureReminderOffset = when (offsetMinutes) {
            "TEN_MINUTES_BEFORE" -> LectureReminderOffset.TEN_MINUTES_BEFORE
            "ZERO_MINUTE" -> LectureReminderOffset.AT_START_TIME
            "TEN_MINUTES_AFTER" -> LectureReminderOffset.TEN_MINUTES_AFTER
            else -> LectureReminderOffset.NONE
        },
    )
