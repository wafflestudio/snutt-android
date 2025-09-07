package com.wafflestudio.snutt2.domainmodel.preview

import com.wafflestudio.snutt2.domainmodel.diary.DiaryQuestionAnswer
import com.wafflestudio.snutt2.domainmodel.diary.DiarySummary
import com.wafflestudio.snutt2.lib.network.dto.core.CourseBookDto
import java.time.LocalDate

object DiaryPreviewData {
    val courseBookDtoList = listOf(
        CourseBookDto(semester = 3, year = 24),
        CourseBookDto(semester = 2, year = 24),
        CourseBookDto(semester = 1, year = 24),
        CourseBookDto(semester = 4, year = 23),
        CourseBookDto(semester = 3, year = 23),
        CourseBookDto(semester = 2, year = 23),
        CourseBookDto(semester = 1, year = 23),
        CourseBookDto(semester = 4, year = 22),
    )

    val diaryList = mapOf(
        LocalDate.of(2024, 3, 20) to listOf(
            DiarySummary(
                lectureName = "시각디자인기초",
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
                lectureName = "배구",
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
                comment = "오티 했어용. 교수님이 과제량 많다고 하셨는데 도움이 많이 될 것 같아 기대가 돼요. 수업 들으려고 과외도 끊었지 뭐에요 \uD83D\uDE2E\u200D\uD83D\uDCA8",
            ),

            ),
        LocalDate.of(2024, 3, 19) to listOf(
            DiarySummary(
                lectureName = "시각디자인기초",
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
                comment = "오티 했어용. 교수님이 과제량 많다고 하셨는데 도움이 많이 될 것 같아 기대가 돼요. 수업 들으려고 과외도 끊었지 뭐에요 \uD83D\uDE2E\u200D\uD83D\uDCA8오티 했어용. 교수님이 과제량 많다고 하셨는데 도움이 많이 될 것 같아 기대가 돼요. 수업 들으려고 과외도 끊었지 뭐에요 \uD83D\uDE2E\u200D\uD83D\uDCA8",
            ),
            DiarySummary(
                lectureName = "배구",
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
                comment = "오티 했어용. 교수님이 과제량 많다고 하셨는데 도움이 많이 될 것 같아 기대가 돼요. 수업 들으려고 과외도 끊었지 뭐에요 \uD83D\uDE2E\u200D\uD83D\uDCA8",
            ),
        ),
    )
}
