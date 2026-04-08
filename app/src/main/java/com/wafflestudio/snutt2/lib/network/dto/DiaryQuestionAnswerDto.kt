package com.wafflestudio.snutt2.lib.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DiaryQuestionAnswerDto(
    @param:Json(name = "questionId") val questionId: String,
    @param:Json(name = "answerIndex") val answerIndex: Int,
)
