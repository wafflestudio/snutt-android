package com.wafflestudio.snutt2.lib.network.dto.core

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.domainmodel.diary.DiaryQuestionAnswer
import com.wafflestudio.snutt2.domainmodel.diary.DiarySummary
import java.time.LocalDateTime

@JsonClass(generateAdapter = true)
data class DiarySubmissionSummaryDto(
    @param:Json(name = "id") val id: String,
    @param:Json(name = "lectureId") val lectureId: String,
    @param:Json(name = "date") val date: String,
    @param:Json(name = "lectureTitle") val lectureTitle: String,
    @param:Json(name = "shortQuestionReplies") val shortQuestionReplies: List<DiaryShortQuestionReplyDto>,
    @param:Json(name = "comment") val comment: String,
)

fun DiarySubmissionSummaryDto.toDomainModel(): DiarySummary {
    return DiarySummary(
        id = id,
        lectureId = lectureId,
        lectureName = lectureTitle,
        date = LocalDateTime.parse(date),
        questionAnswers = shortQuestionReplies.map {
            DiaryQuestionAnswer(
                question = it.question,
                answer = it.answer,
            )
        },
        comment = comment.ifBlank { null },
    )
}
