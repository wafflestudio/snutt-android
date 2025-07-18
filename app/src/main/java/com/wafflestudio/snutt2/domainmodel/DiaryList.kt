package com.wafflestudio.snutt2.domainmodel

import com.wafflestudio.snutt2.lib.network.dto.core.CourseBookDto
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

data class DiaryList(
    val courseBook: CourseBookDto,
    val diaryList: Map<LocalDate, List<DiaryListLectureItem>>,
)
