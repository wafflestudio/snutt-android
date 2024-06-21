package com.wafflestudio.snutt2.core.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.lib.network.dto.core.LectureBuildingDto

@JsonClass(generateAdapter = true)
data class BuildingsResponse(
    @Json(name = "content") val content: List<LectureBuildingDto>,
    @Json(name = "totalCount") val totalCount: Int,
)
