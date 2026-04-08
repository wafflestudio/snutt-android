package com.wafflestudio.snutt2.lib.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DiaryQuestionDto(
    @param:Json(name = "id") val id: String,
    @param:Json(name = "question") val question: String,
    @param:Json(name = "answers") val answers: List<String>,
)
