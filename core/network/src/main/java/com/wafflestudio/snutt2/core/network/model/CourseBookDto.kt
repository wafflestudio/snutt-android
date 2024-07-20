package com.wafflestudio.snutt2.core.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CourseBookDto(
    @Json(name = "semester") val semester: Long,
    @Json(name = "year") val year: Long,
)