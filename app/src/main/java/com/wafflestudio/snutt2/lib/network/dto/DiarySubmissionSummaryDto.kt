package com.wafflestudio.snutt2.lib.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DiarySubmissionSummaryDto(
    @param:Json(name = "id") val id: String,
    @param:Json(name = "lectureId") val lectureId: String,
    @param:Json(name = "date") val date: String,
    @param:Json(name = "courseTitle") val courseTitle: String,
    @param:Json(name = "shortQuestionReplies") val shortQuestionReplies: List<DiaryShortQuestionReplyDto>,
    @param:Json(name = "comment") val comment: String,
)
