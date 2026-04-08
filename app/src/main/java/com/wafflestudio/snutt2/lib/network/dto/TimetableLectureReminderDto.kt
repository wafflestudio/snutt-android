package com.wafflestudio.snutt2.lib.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// NOTE: 강의 리마인더가 지정된 적 없는 강의의 경우 empty response가 오는데 그 경우 EOFException이 나게 되고, ExceptionMapper에서 DomainError.EOF로 변환한다.
@JsonClass(generateAdapter = true)
data class TimetableLectureReminderDto(
    @param:Json(name = "timetableLectureId") val lectureId: String,
    @param:Json(name = "courseTitle") val courseTitle: String,
    @param:Json(name = "option") val offsetMinutes: String,
)
