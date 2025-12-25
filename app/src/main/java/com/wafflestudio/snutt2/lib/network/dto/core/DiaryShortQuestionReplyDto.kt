package com.wafflestudio.snutt2.lib.network.dto.core

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.domainmodel.diary.DiaryQuestionAnswer

@JsonClass(generateAdapter = true)
data class DiaryShortQuestionReplyDto(
    @param:Json(name = "question") val question: String,
    @param:Json(name = "answer") val answer: String,
)

fun DiaryShortQuestionReplyDto.toDomainModel(): DiaryQuestionAnswer {
    return DiaryQuestionAnswer(
        question = question,
        answer = answer,
    )
}
