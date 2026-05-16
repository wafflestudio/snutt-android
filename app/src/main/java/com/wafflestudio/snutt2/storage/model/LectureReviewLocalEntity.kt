package com.wafflestudio.snutt2.storage.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LectureReviewLocalEntity(
    @param:Json(name = "evLectureId") val id: String,
    @param:Json(name = "avgRating") val rating: Double? = null,
    @param:Json(name = "evaluationCount") val reviewCount: Int? = null,
)
