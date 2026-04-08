package com.wafflestudio.snutt2.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DiaryQuestionnaireRequestDto(
    @param:Json(name = "lectureId") val lectureId: String,
    @param:Json(name = "dailyClassTypes") val dailyClassTypes: List<String>,
)
