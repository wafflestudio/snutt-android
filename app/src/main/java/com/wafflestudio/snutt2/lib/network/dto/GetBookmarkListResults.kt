package com.wafflestudio.snutt2.lib.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.lib.network.dto.LectureDto

@JsonClass(generateAdapter = true)
data class GetBookmarkListResults(
    @param:Json(name = "year") val year: Long,
    @param:Json(name = "semester") val semester: Long,
    @param:Json(name = "lectures") val lectures: List<LectureDto>,
)
