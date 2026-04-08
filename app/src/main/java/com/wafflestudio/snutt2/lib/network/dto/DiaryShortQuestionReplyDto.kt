package com.wafflestudio.snutt2.lib.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DiaryShortQuestionReplyDto(
    @param:Json(name = "question") val question: String,
    @param:Json(name = "answer") val answer: String,
)
