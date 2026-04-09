package com.wafflestudio.snutt2.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BuildingsResponse(
    @param:Json(name = "content") val content: List<LectureBuildingDto>,
    @param:Json(name = "totalCount") val totalCount: Int,
)
