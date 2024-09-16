package com.wafflestudio.snutt2.core.network.model

import android.content.Context
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.core.network.R

@JsonClass(generateAdapter = true)
data class LectureReviewDto(
    @Json(name = "evLectureId") val id: String,
    @Json(name = "avgRating") val rating: Double? = null,
    @Json(name = "evaluationCount") val reviewCount: Int? = null,
) {
    val ratingDisplayText get() = rating?.times(10)?.toInt()?.div(10.0)?.toString() ?: "--"
    val displayText get() = "$ratingDisplayText (${reviewCount ?: 0})"

    fun getReviewUrl(context: Context): String? { // DTO와 ui model 분리 후 ui model으로 옮기기
        return id.let {
            context.getString(R.string.review_base_url) + "/detail?id=$it"
        }
    }
}
