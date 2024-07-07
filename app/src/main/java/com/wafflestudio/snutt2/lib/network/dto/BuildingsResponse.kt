package com.wafflestudio.snutt2.lib.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.lib.network.dto.core.LectureBuildingDto
import com.wafflestudio.snutt2.lib.network.dto.core.toExternalModel
import com.wafflestudio.snutt2.core.network.model.BuildingsResponse as BuildingsResponseNetwork

@JsonClass(generateAdapter = true)
data class BuildingsResponse(
    @Json(name = "content") val content: List<LectureBuildingDto>,
    @Json(name = "totalCount") val totalCount: Int,
)

fun BuildingsResponseNetwork.toExternalModel() = BuildingsResponse(
    content = this.content.map { it.toExternalModel() },
    totalCount = this.totalCount,
)