package com.wafflestudio.snutt2.lib.network.dto.core

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SharedTableDto(
    @Json(name = "id") val id: String,
    @Json(name = "title") val title: String,
    @Json(name = "timetable") val timetable: TableDto,
)
