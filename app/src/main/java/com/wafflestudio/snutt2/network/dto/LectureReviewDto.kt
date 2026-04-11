package com.wafflestudio.snutt2.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LectureReviewDto(
    @param:Json(name = "evLectureId") val id: String,
    @param:Json(name = "avgRating") val rating: Double? = null,
    @param:Json(name = "evaluationCount") val reviewCount: Int? = null,
)
