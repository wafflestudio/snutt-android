package com.wafflestudio.snutt2.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetVacancyLecturesResultsT(
    @Json(name = "lectures") val lectures: List<LectureDtoT>,
)
