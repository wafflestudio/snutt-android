package com.wafflestudio.snutt2.lib.network.dto.core

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DiaryQuestionnaireDto(
    @param:Json(name = "lectureTitle") val lectureTitle: String,
    @param:Json(name = "questions") val questions: List<DiaryQuestionDto>,
    @param:Json(name = "nextLectureId") val nextLectureId: String? = null,
    @param:Json(name = "nextLectureTitle") val nextLectureTitle: String? = null,
)
