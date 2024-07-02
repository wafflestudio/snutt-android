package com.wafflestudio.snutt2.lib.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.core.network.model.GetVacancyLecturesResults as GetVacancyLecturesResultsNetwork
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.lib.network.dto.core.toExternalModel

@JsonClass(generateAdapter = true)
data class GetVacancyLecturesResults(
    @Json(name = "lectures") val lectures: List<LectureDto>,
)

fun GetVacancyLecturesResultsNetwork.toExternalModel() = GetVacancyLecturesResults(
    lectures = this.lectures.map { it.toExternalModel() },
)