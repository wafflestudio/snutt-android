package com.wafflestudio.snutt2.lib.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PostSharedTableParams(
    @Json(name = "title") val title: String,
    @Json(name = "timetable_id") val timetableId: String,
)
