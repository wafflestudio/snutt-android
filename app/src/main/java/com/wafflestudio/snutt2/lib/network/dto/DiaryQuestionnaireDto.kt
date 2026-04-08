package com.wafflestudio.snutt2.lib.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DiaryQuestionnaireDto(
    @param:Json(name = "courseTitle") val courseTitle: String,
    @param:Json(name = "questions") val questions: List<DiaryQuestionDto>,
    @param:Json(name = "nextLecture") val nextLecture: NextLectureDto? = null,
) {
    @JsonClass(generateAdapter = true)
    data class NextLectureDto(
        @param:Json(name = "lectureId") val lectureId: String,
        @param:Json(name = "courseTitle") val courseTitle: String,
    )
}
