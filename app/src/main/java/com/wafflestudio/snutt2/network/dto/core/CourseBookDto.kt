package com.wafflestudio.snutt2.network.dto.core

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CourseBookDto(
    @Json(name = "_id") val id: String,
    @Json(name = "semester") val semester: Long,
    @Json(name = "year") val year: Long
)
