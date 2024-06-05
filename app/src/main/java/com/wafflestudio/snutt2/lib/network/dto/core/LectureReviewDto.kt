package com.wafflestudio.snutt2.lib.network.dto.core

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LectureReviewDto(
    @Json(name = "evLectureId") val id: String,
    @Json(name = "avgRating") val rating: Double? = null,
    @Json(name = "evaluationCount") val reviewCount: Int? = null,
) {
    val ratingDisplayText get() = rating?.times(10)?.toInt()?.div(10.0)?.toString() ?: "--"
    val displayText get() = "$ratingDisplayText (${reviewCount ?: 0})"
}
