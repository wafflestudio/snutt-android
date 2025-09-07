package com.wafflestudio.snutt2.domainmodel.diary

import com.wafflestudio.snutt2.lib.Selectable
import java.time.LocalDate

// TODO: 파일 분리
data class DiaryActivity(
    val id: String,
    val name: String,
)

data class DiaryQuestion(
    val id: String,
    val question: String,
    val selectableAnswers: List<Selectable<String>>,
)

data class DiaryAnsweredQuestion(
    val questionId: String,
    val answerIndex: Int,
)

data class DiaryQuestionAnswer(
    val question: String,
    val answer: String,
)

data class DiarySummary(
    val lectureName: String,
    val questionAnswers: List<DiaryQuestionAnswer>,
    val comment: String?,
)

typealias DiarySummariesByDate = Map<LocalDate, List<DiarySummary>>