package com.wafflestudio.snutt2.lib.network.dto.core

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LectureReviewDto(
    @Json(name = "evLectureId") val id: String,
    @Json(name = "avgRating") val rating: Double? = null,
)
