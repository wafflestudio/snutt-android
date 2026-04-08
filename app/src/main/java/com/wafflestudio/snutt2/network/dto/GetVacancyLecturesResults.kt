package com.wafflestudio.snutt2.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.network.dto.LectureDto

@JsonClass(generateAdapter = true)
data class GetVacancyLecturesResults(
    @param:Json(name = "lectures") val lectures: List<LectureDto>,
)
