package com.wafflestudio.snutt2.storage.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ClassTimeLocalEntity(
    @param:Json(name = "day") val day: Int,
    @param:Json(name = "place") val place: String,
    @param:Json(name = "_id") val id: String? = null,
    @param:Json(name = "startMinute") val startMinute: Int = 0,
    @param:Json(name = "endMinute") val endMinute: Int = 0,
)
