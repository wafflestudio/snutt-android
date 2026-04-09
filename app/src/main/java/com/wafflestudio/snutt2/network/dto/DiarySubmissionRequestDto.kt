package com.wafflestudio.snutt2.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.network.dto.DiaryQuestionAnswerDto

@JsonClass(generateAdapter = true)
data class DiarySubmissionRequestDto(
    @param:Json(name = "lectureId") val lectureId: String,
    @param:Json(name = "dailyClassTypes") val dailyClassTypes: List<String>,
    @param:Json(name = "questionAnswers") val questionAnswers: List<DiaryQuestionAnswerDto>,
    @param:Json(name = "comment") val comment: String,
)
