package com.wafflestudio.snutt2.core.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BuildingsResponseT(
    @Json(name = "content") val content: List<LectureBuildingDtoT>,
    @Json(name = "totalCount") val totalCount: Int,
)
