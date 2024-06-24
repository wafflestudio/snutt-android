package com.wafflestudio.snutt2.core.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetVacancyLecturesResults(
    @Json(name = "lectures") val lectures: List<LectureDto>,
)
