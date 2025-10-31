package com.wafflestudio.snutt2.lib.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PutTimetableLectureReminderParams(
    @param:Json(name = "option") val offsetMinutes: String,
)
