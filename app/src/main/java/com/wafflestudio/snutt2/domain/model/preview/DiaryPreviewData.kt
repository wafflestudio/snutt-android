package com.wafflestudio.snutt2.domain.model.preview

import com.wafflestudio.snutt2.domain.model.CourseBook
import com.wafflestudio.snutt2.domain.model.diary.DiaryQuestionAnswer
import com.wafflestudio.snutt2.domain.model.diary.DiarySummary
import com.wafflestudio.snutt2.lib.toDataWithState
import java.time.LocalDate
import java.time.LocalDateTime

object DiaryPreviewData {
    val sampleDiarySummaryLongComment = DiarySummary(
        id = "preview-id-1",
        lectureId = "",
        courseName = "시각디자인기초",
        date = LocalDateTime.of(2025, 3, 20, 12, 0),
        questionAnswers = listOf(
            DiaryQuestionAnswer(question = "수강신청", answer = "널널해요"),
            DiaryQuestionAnswer(question = "드랍여부", answer = "안했어요"),
            DiaryQuestionAnswer(question = "수업 첫인상", answer = "하.."),
        ),
        comment = "좋아요.좋아요.좋아요.좋아요.좋아요.좋아요.좋아요.좋아요.좋아요.좋아요.좋아요.좋아요.좋아요.좋아요.좋아요.좋아요.좋아요.좋아요.좋아요.좋아요.",
    )

    val sampleDiarySummaryShortComment = DiarySummary(
        id = "preview-id-2",
        lectureId = "",
        courseName = "배구",
        date = LocalDateTime.of(2025, 3, 20, 12, 0),
        questionAnswers = listOf(
            DiaryQuestionAnswer(question = "수강신청", answer = "널널해요"),
            DiaryQuestionAnswer(question = "드랍여부", answer = "안했어요"),
            DiaryQuestionAnswer(question = "수업 첫인상", answer = "하.."),
        ),
        comment = "좋아요",
    )

    val courseBookList = listOf(
        CourseBook(semester = 3, year = 2024),
        CourseBook(semester = 2, year = 2024),
        CourseBook(semester = 1, year = 2024),
        CourseBook(semester = 4, year = 2023),
        CourseBook(semester = 3, year = 2023),
        CourseBook(semester = 2, year = 2023),
        CourseBook(semester = 1, year = 2023),
        CourseBook(semester = 4, year = 2022),
    )

    val diaryList = mapOf(
        LocalDate.of(2024, 3, 20) to listOf(
            DiarySummary(
                id = "diary-id-1",
                lectureId = "aaaaa시각디자인기초",
                courseName = "시각디자인기초",
                date = java.time.LocalDateTime.of(2024, 3, 20, 10, 0),
                questionAnswers = listOf(
                    DiaryQuestionAnswer(
                        question = "수강신청",
                        answer = "널널해요",
                    ),
                    DiaryQuestionAnswer(
                        question = "드랍여부",
                        answer = "모르겠어요",
                    ),
                    DiaryQuestionAnswer(
                        question = "수업 첫인상",
                        answer = "널널해요",
                    ),
                ),
                comment = "좋아요",
            ),
            DiarySummary(
                id = "diary-id-2",
                lectureId = "bbbb배구",
                courseName = "배구",
                date = java.time.LocalDateTime.of(2024, 3, 20, 14, 0),
                questionAnswers = listOf(
                    DiaryQuestionAnswer(
                        question = "수강신청",
                        answer = "널널해요",
                    ),
                    DiaryQuestionAnswer(
                        question = "드랍여부",
                        answer = "모르겠어요",
                    ),
                    DiaryQuestionAnswer(
                        question = "수업 첫인상",
                        answer = "널널해요",
                    ),
                ),
                comment = "오티 했어용. 교수님이 과제량 많다고 하셨는데 도움이 많이 될 것 같아 기대가 돼요. 수업 들으려고 과외도 끊었지 뭐에요 😮‍💨",
            ),

        ).toDataWithState(true),
        LocalDate.of(2024, 3, 19) to listOf(
            DiarySummary(
                id = "diary-id-3",
                lectureId = "cccc시각디자인기초",
                courseName = "시각디자인기초",
                date = java.time.LocalDateTime.of(2024, 3, 19, 10, 0),
                questionAnswers = listOf(
                    DiaryQuestionAnswer(
                        question = "수강신청",
                        answer = "널널해요",
                    ),
                    DiaryQuestionAnswer(
                        question = "드랍여부",
                        answer = "모르겠어요",
                    ),
                    DiaryQuestionAnswer(
                        question = "수업 첫인상",
                        answer = "널널해요",
                    ),
                ),
                comment = "오티 했어용. 교수님이 과제량 많다고 하셨는데 도움이 많이 될 것 같아 기대가 돼요. 수업 들으려고 과외도 끊었지 뭐에요 😮‍💨오티 했어용. 교수님이 과제량 많다고 하셨는데 도움이 많이 될 것 같아 기대가 돼요. 수업 들으려고 과외도 끊었지 뭐에요 😮‍💨",
            ),
            DiarySummary(
                id = "diary-id-4",
                lectureId = "dddd배구",
                courseName = "배구",
                date = java.time.LocalDateTime.of(2024, 3, 19, 14, 0),
                questionAnswers = listOf(
                    DiaryQuestionAnswer(
                        question = "수강신청",
                        answer = "널널해요",
                    ),
                    DiaryQuestionAnswer(
                        question = "드랍여부",
                        answer = "모르겠어요",
                    ),
                    DiaryQuestionAnswer(
                        question = "수업 첫인상",
                        answer = "널널해요",
                    ),
                ),
                comment = "오티 했어용. 교수님이 과제량 많다고 하셨는데 도움이 많이 될 것 같아 기대가 돼요. 수업 들으려고 과외도 끊었지 뭐에요 😮‍💨",
            ),
        ).toDataWithState(false),
    )
}
