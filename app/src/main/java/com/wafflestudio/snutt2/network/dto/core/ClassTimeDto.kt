package com.wafflestudio.snutt2.network.dto.core

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ClassTimeDto(
    @Json(name = "day") val day: Int,
    @Json(name = "start") val start: Float,
    @Json(name = "len") val len: Float,
    @Json(name = "place") val place: String,
    @Json(name = "_id") val id: String? = null
)
