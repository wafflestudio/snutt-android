package com.wafflestudio.snutt2.lib.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto

@JsonClass(generateAdapter = true)
data class GetVacancyLecturesResults(
    @Json(name = "lectures") val lectures: List<LectureDto>,
)
