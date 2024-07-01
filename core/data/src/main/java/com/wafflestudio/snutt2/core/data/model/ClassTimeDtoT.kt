package com.wafflestudio.snutt2.core.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ClassTimeDtoT(
    @Json(name = "day") val day: Int,
    @Json(name = "place") val place: String,
    @Json(name = "_id") val id: String? = null,
    @Json(name = "startMinute") val startMinute: Int = 0,
    @Json(name = "endMinute") val endMinute: Int = 0,
)
