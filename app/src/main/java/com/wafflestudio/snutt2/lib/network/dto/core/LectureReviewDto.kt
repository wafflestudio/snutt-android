package com.wafflestudio.snutt2.lib.network.dto.core

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LectureReviewDto(
    @Json(name = "evLectureId") val id: String,
    @Json(name = "avgRating") val rating: Double? = null,
    val numReviews: Int? = null,
) {
    val ratingDisplayText get() = rating?.times(10)?.toInt()?.div(10.0)?.toString() ?: "--"
    val displayText get() = "$ratingDisplayText (${numReviews ?: 0})"
}
