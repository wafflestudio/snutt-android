package com.wafflestudio.snutt2.lib.network.dto.core

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.domainmodel.diary.DiaryQuestion
import com.wafflestudio.snutt2.lib.Selectable

@JsonClass(generateAdapter = true)
data class DiaryQuestionDto(
    @param:Json(name = "id") val id: String,
    @param:Json(name = "question") val question: String,
    @param:Json(name = "answers") val answers: List<String>,
)

fun DiaryQuestionDto.toDomainModel(): DiaryQuestion {
    return DiaryQuestion(
        id = id,
        question = question,
        selectableAnswers = answers.map { Selectable(it, false) },
    )
}
