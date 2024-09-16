package com.wafflestudio.snutt2.core.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetBookmarkListResults(
    @Json(name = "year") val year: Long,
    @Json(name = "semester") val semester: Long,
    @Json(name = "lectures") val lectures: List<LectureDto>,
)
