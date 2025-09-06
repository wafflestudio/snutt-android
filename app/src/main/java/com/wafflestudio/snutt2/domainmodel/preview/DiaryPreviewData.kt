package com.wafflestudio.snutt2.domainmodel.preview

import com.wafflestudio.snutt2.domainmodel.DiaryQuestionAnswer
import com.wafflestudio.snutt2.domainmodel.DiaryWrite
import com.wafflestudio.snutt2.domainmodel.DiaryWriteQuestion
import com.wafflestudio.snutt2.lib.network.dto.core.CourseBookDto
import java.time.LocalDate

object DiaryPreviewData {

    val diaryWriteQuestion =
        DiaryWriteQuestion(
            question = "수업 끝까지 들을 것 같나요?",
            options = listOf("끝까지 들을 거에요", "모르겠어요", "드랍할 것 같아요"),
        )
    val diaryWriteInit = DiaryWrite(
        "알고리즘",
        todayState = listOf(
            false, true, true, false, false, false, false, false,
        ),
        questionsState = listOf(
            listOf(true, false, false),
            listOf(true, false, false),
            listOf(false, true, false),
        ),
        moreText = "좋았어요 ㅇㅅㅇ",
    )
    val diaryWriteNewInit = DiaryWrite(
        "알고리즘",
        null, null, "",
    )

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
            com.wafflestudio.snutt2.domainmodel.DiaryListLectureItem(
                lectureName = "시각디자인기초",
                content = listOf(
                    DiaryQuestionAnswer(question = "수강신청", answer = "널널해요"),
                    DiaryQuestionAnswer(question = "드랍여부", answer = "모르겠어요"),
                    DiaryQuestionAnswer(question = "수업 첫인상", answer = "널널해요"),
                ),
                moreText = "좋아요",
            ),
            com.wafflestudio.snutt2.domainmodel.DiaryListLectureItem(
                lectureName = "배구",
                content = listOf(
                    DiaryQuestionAnswer(question = "수강신청", answer = "널널해요"),
                    DiaryQuestionAnswer(question = "드랍여부", answer = "모르겠어요"),
                    DiaryQuestionAnswer(question = "수업 첫인상", answer = "널널해요"),
                ),
                moreText = "오티 했어용. 교수님이 과제량 많다고 하셨는데 도움이 많이 될 것 같아 기대가 돼요. 수업 들으려고 과외도 끊었지 뭐에요 \uD83D\uDE2E\u200D\uD83D\uDCA8",
            ),

        ),
        LocalDate.of(2024, 3, 19) to listOf(
            com.wafflestudio.snutt2.domainmodel.DiaryListLectureItem(
                lectureName = "시각디자인기초",
                content = listOf(
                    DiaryQuestionAnswer(question = "수강신청", answer = "널널해요"),
                    DiaryQuestionAnswer(question = "드랍여부", answer = "모르겠어요"),
                    DiaryQuestionAnswer(question = "수업 첫인상", answer = "널널해요"),
                ),
                moreText = "오티 했어용. 교수님이 과제량 많다고 하셨는데 도움이 많이 될 것 같아 기대가 돼요. 수업 들으려고 과외도 끊었지 뭐에요 \uD83D\uDE2E\u200D\uD83D\uDCA8오티 했어용. 교수님이 과제량 많다고 하셨는데 도움이 많이 될 것 같아 기대가 돼요. 수업 들으려고 과외도 끊었지 뭐에요 \uD83D\uDE2E\u200D\uD83D\uDCA8",
            ),
            com.wafflestudio.snutt2.domainmodel.DiaryListLectureItem(
                lectureName = "배구",
                content = listOf(
                    DiaryQuestionAnswer(question = "수강신청", answer = "널널해요"),
                    DiaryQuestionAnswer(question = "드랍여부", answer = "모르겠어요"),
                    DiaryQuestionAnswer(question = "수업 첫인상", answer = "널널해요"),
                ),
                moreText = "오티 했어용. 교수님이 과제량 많다고 하셨는데 도움이 많이 될 것 같아 기대가 돼요. 수업 들으려고 과외도 끊었지 뭐에요 \uD83D\uDE2E\u200D\uD83D\uDCA8",
            ),
        ),
    )
}
