package com.wafflestudio.snutt2.lib.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto

@JsonClass(generateAdapter = true)
data class GetBookmarkListResults(
    @Json(name = "year") val year: Long,
    @Json(name = "semester") val semester: Long,
    @Json(name = "lectures") val lectures: List<LectureDto>,
)
