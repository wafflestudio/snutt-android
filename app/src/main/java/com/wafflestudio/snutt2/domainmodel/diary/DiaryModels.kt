package com.wafflestudio.snutt2.domainmodel.diary

import com.wafflestudio.snutt2.domainmodel.CourseBook
import com.wafflestudio.snutt2.lib.Selectable

// TODO: 파일 분리
data class DiaryDailyClassType(
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
    val id: String,
    val lectureId: String,
    val lectureName: String,
    val date: java.time.LocalDateTime,
    val questionAnswers: List<DiaryQuestionAnswer>,
    val comment: String?,
)

data class CourseBookDiarySubmissions(
    val courseBook: CourseBook,
    val submissions: List<DiarySummary>,
)
