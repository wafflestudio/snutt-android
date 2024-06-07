package com.wafflestudio.snutt2.lib.network.dto.core

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BuildingsResponse(
    @Json(name = "content") val content: List<LectureBuildingDto>,
    @Json(name = "totalCount") val totalCount: Int,
)
