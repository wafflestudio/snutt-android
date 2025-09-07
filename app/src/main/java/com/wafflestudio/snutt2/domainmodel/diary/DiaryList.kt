package com.wafflestudio.snutt2.domainmodel.diary

import java.time.LocalDate

data class DiaryQuestionAnswer(
    val question: String,
    val answer: String,
)

data class DiaryListLectureItem(
    val lectureName: String,
    val content: List<DiaryQuestionAnswer>,
    val moreText: String?,
)

typealias DiaryList = Map<LocalDate, List<DiaryListLectureItem>>
