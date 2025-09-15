package com.wafflestudio.snutt2.lib.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.lib.network.dto.core.TimetableLectureReminderDto

@JsonClass(generateAdapter = true)
data class GetActiveLectureRemindersResults(
    @param:Json(name = "timetableId") val timetableId: String,
    @param:Json(name = "reminders") val reminders: List<TimetableLectureReminderDto>,
)
