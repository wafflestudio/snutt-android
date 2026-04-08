package com.wafflestudio.snutt2.data.mapper

import com.wafflestudio.snutt2.domainmodel.CourseBook
import com.wafflestudio.snutt2.domainmodel.diary.CourseBookDiarySubmissions
import com.wafflestudio.snutt2.domainmodel.diary.DiaryDailyClassType
import com.wafflestudio.snutt2.domainmodel.diary.DiaryQuestion
import com.wafflestudio.snutt2.domainmodel.diary.DiaryQuestionAnswer
import com.wafflestudio.snutt2.domainmodel.diary.DiarySummary
import com.wafflestudio.snutt2.lib.Selectable
import com.wafflestudio.snutt2.lib.network.dto.DiaryDailyClassTypeDto
import com.wafflestudio.snutt2.lib.network.dto.DiaryQuestionDto
import com.wafflestudio.snutt2.lib.network.dto.DiaryShortQuestionReplyDto
import com.wafflestudio.snutt2.lib.network.dto.DiarySubmissionSummaryDto
import com.wafflestudio.snutt2.lib.network.dto.DiarySubmissionsOfYearSemesterDto
import java.time.LocalDateTime

fun DiaryQuestionDto.toDomain(): DiaryQuestion = DiaryQuestion(
    id = id,
    question = question,
    selectableAnswers = answers.map { Selectable(it, false) },
)

fun DiaryDailyClassTypeDto.toDomain(): DiaryDailyClassType = DiaryDailyClassType(
    id = id,
    name = name,
)

fun DiaryShortQuestionReplyDto.toDomain(): DiaryQuestionAnswer = DiaryQuestionAnswer(
    question = question,
    answer = answer,
)

fun DiarySubmissionSummaryDto.toDomain(): DiarySummary = DiarySummary(
    id = id,
    lectureId = lectureId,
    courseName = courseTitle,
    date = LocalDateTime.parse(date),
    questionAnswers = shortQuestionReplies.map { it.toDomain() },
    comment = comment.ifBlank { null },
)

fun DiarySubmissionsOfYearSemesterDto.toDomain(): CourseBookDiarySubmissions = CourseBookDiarySubmissions(
    courseBook = CourseBook(
        year = year.toLong(),
        semester = semester.toLong(),
    ),
    submissions = submissions.map { it.toDomain() },
)
