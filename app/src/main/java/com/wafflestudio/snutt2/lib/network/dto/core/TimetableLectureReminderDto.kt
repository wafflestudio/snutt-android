package com.wafflestudio.snutt2.lib.network.dto.core

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.domainmodel.LectureWithReminderOption
import com.wafflestudio.snutt2.domainmodel.LectureReminderOffset

// NOTE: 강의 리마인더가 지정된 적 없는 강의의 경우 empty response가 오는데 그 경우 EOFException이 나게 되고, ExceptionMapper에서 DomainError.EOF로 변환한다.
@JsonClass(generateAdapter = true)
data class TimetableLectureReminderDto(
    @Json(name = "id") val reminderId: String,
    @Json(name = "timetableLectureId") val lectureId: String,
    @Json(name = "offsetMinutes") val offsetMinutes: Int,
)

fun TimetableLectureReminderDto.toDomainModel(): LectureWithReminderOption = LectureWithReminderOption(
    lectureId = lectureId,
    lectureTitle = "",
    lectureReminderOffset = when (offsetMinutes) {
        -10 -> LectureReminderOffset.TEN_MINUTES_BEFORE
        0 -> LectureReminderOffset.AT_START_TIME
        10 -> LectureReminderOffset.TEN_MINUTES_AFTER
        else -> LectureReminderOffset.NONE
    },
)
