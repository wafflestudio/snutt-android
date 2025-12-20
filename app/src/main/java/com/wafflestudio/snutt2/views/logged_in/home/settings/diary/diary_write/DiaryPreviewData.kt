package com.wafflestudio.snutt2.views.logged_in.home.settings.diary.diary_write

import com.wafflestudio.snutt2.domainmodel.diary.DiaryDailyClassType
import com.wafflestudio.snutt2.domainmodel.diary.DiaryQuestion
import com.wafflestudio.snutt2.lib.Selectable

object DiaryPreviewData {
    val dailyClassTypes = listOf(
        DiaryDailyClassType("1", "개강"),
        DiaryDailyClassType("2", "수업"),
        DiaryDailyClassType("3", "실기"),
        DiaryDailyClassType("4", "시험"),
        DiaryDailyClassType("5", "발표"),
        DiaryDailyClassType("6", "휴강"),
        DiaryDailyClassType("7", "종강"),
        DiaryDailyClassType("8", "드랍"),
    )

    val selectableDailyClassTypes = dailyClassTypes.map { dailyClassType ->
        Selectable(dailyClassType, false)
    }

    val selectableDailyClassTypesSelected =
        dailyClassTypes.mapIndexed { index, dailyClassType ->
            Selectable(
                dailyClassType,
                index == 1,
            ) // "수업" selected
        }

    fun getQuestionsForActivities(
        selectedActivities: List<String>,
        lectureName: String = "컴퓨터프로그래밍",
    ): List<DiaryQuestion> {
        return when {
            selectedActivities.contains("개강") -> listOf(
                DiaryQuestion(
                    "q1",
                    "수강신청은 쉬웠나요?",
                    listOf(
                        Selectable("널널했어요", false),
                        Selectable("2~3픽 했어요", true),
                        Selectable("1픽 했어요", false),
                        Selectable("초안지 썼어요", false),
                    ),
                ),
                DiaryQuestion(
                    "q2",
                    "교수님/수업 첫인상은 어땠나요?",
                    listOf(
                        Selectable("즐거울 것 같아요", true),
                        Selectable("별 생각 없어요", false),
                        Selectable("큰일 난 것 같아요", false),
                    ),
                ),
                DiaryQuestion(
                    "q3",
                    "수업 끝까지 들을 것 같나요?",
                    listOf(
                        Selectable("끝까지 들을 거에요", true),
                        Selectable("모르겠어요", false),
                        Selectable("드랍할 것 같아요", false),
                    ),
                ),
            )

            selectedActivities.contains("시험") -> listOf(
                DiaryQuestion(
                    "q1",
                    "시험 잘 보셨나요?",
                    listOf(
                        Selectable("잘 본 것 같아요", false),
                        Selectable("공부한만큼 풀었어요", true),
                        Selectable("조용히 하세요", false),
                    ),
                ),
                DiaryQuestion(
                    "q2",
                    "무슨 시험이었나요?",
                    listOf(
                        Selectable("중간", true),
                        Selectable("기말", false),
                        Selectable("1차", false),
                        Selectable("2차", false),
                        Selectable("3차", false),
                        Selectable("쪽지시험", false),
                        Selectable("수시", false),
                    ),
                ),
                DiaryQuestion(
                    "q3",
                    "어떤 시험이었나요?",
                    listOf(
                        Selectable("오픈북이었어요", true),
                        Selectable("서술형이었어요", false),
                        Selectable("등등", false),
                    ),
                ),
            )

            selectedActivities.contains("드랍") -> listOf(
                DiaryQuestion(
                    "q1",
                    "왜 드랍하셨나요?",
                    listOf(
                        Selectable("전공수업 들어야 해서요", false),
                        Selectable("흥미가 없어서요", true),
                        Selectable("시험 망해서요", false),
                    ),
                ),
                DiaryQuestion(
                    "q2",
                    "다시 들을 건가요?",
                    listOf(
                        Selectable("네", false),
                        Selectable("고민 중이에요", true),
                        Selectable("아니요", false),
                    ),
                ),
                DiaryQuestion(
                    "q3",
                    "친구들도 $lectureName 수업을 드랍했나요?",
                    listOf(
                        Selectable("아니요", true),
                        Selectable("모르겠어요", false),
                        Selectable("네", false),
                    ),
                ),
            )

            selectedActivities.contains("종강") -> listOf(
                DiaryQuestion(
                    "q1",
                    "${lectureName}은 어떤 수업인가요?",
                    listOf(
                        Selectable("명강이에요👍", true),
                        Selectable("꿀강이에요🍯", false),
                        Selectable("고진감래 ⛰️", false),
                        Selectable("무난해요 😀", false),
                    ),
                ),
                DiaryQuestion(
                    "q2",
                    "다시 들을 건가요?",
                    listOf(
                        Selectable("네", false),
                        Selectable("고민 중이에요", false),
                        Selectable("아니요", true),
                    ),
                ),
                DiaryQuestion(
                    "q3",
                    "친구들도 $lectureName 수업을 드랍했나요?",
                    listOf(
                        Selectable("아니요", true),
                        Selectable("모르겠어요", false),
                        Selectable("네", false),
                    ),
                ),
            )

            selectedActivities.contains("휴강") -> listOf(
                DiaryQuestion(
                    "q1",
                    "왜 휴강했나요?",
                    listOf(
                        Selectable("자체휴강", true),
                        Selectable("모르겠어요", false),
                        Selectable("휴일", false),
                        Selectable("교수님 개인사정", false),
                    ),
                ),
                DiaryQuestion(
                    "q2",
                    "지금 과제가 많나요?",
                    listOf(
                        Selectable("없어요", false),
                        Selectable("할만 했어요", true),
                        Selectable("쌓여 있어요", false),
                    ),
                ),
                DiaryQuestion(
                    "q3",
                    "다음 수업시간에는 무엇을 하나요?",
                    listOf(
                        Selectable("수업", true),
                        Selectable("시험", false),
                        Selectable("실기", false),
                        Selectable("발표", false),
                    ),
                ),
            )

            else -> listOf(
                DiaryQuestion(
                    "q1",
                    "오늘 재미있었나요?",
                    listOf(
                        Selectable("흥미진진했어요", true),
                        Selectable("별 생각 없어요", false),
                        Selectable("시간 낭비였어요", false),
                    ),
                ),
                DiaryQuestion(
                    "q2",
                    "지금 과제가 많나요?",
                    listOf(
                        Selectable("없어요", false),
                        Selectable("할만 했어요", true),
                        Selectable("쌓여 있어요", false),
                    ),
                ),
                DiaryQuestion(
                    "q3",
                    "다음 수업시간에는 무엇을 하나요?",
                    listOf(
                        Selectable("수업", true),
                        Selectable("시험", false),
                        Selectable("실기", false),
                        Selectable("발표", false),
                    ),
                ),
            )
        }
    }

    val initialWriteUiState = DiaryWriteUiState.Write(
        lectureName = "컴퓨터프로그래밍",
        activitySelectingState = ActivitySelectionState.InitialSelecting,
        dailyClassTypes = selectableDailyClassTypes,
        questions = getQuestionsForActivities(listOf()).map { question ->
            DiaryQuestion(
                question.id,
                question.question,
                question.selectableAnswers.map { answer ->
                    Selectable(answer.item, false)
                },
            )
        },
    )

    val sampleWriteUiState = DiaryWriteUiState.Write(
        lectureName = "컴퓨터프로그래밍",
        activitySelectingState = ActivitySelectionState.Complete,
        dailyClassTypes = selectableDailyClassTypesSelected,
        questions = getQuestionsForActivities(
            listOf("수업"),
            "컴퓨터프로그래밍",
        ),
    )

    val sampleWriteUiStateSelecting =
        DiaryWriteUiState.Write(
            lectureName = "데이터구조",
            activitySelectingState = ActivitySelectionState.InitialSelecting,
            dailyClassTypes = dailyClassTypes.mapIndexed { index, dailyClassType ->
                Selectable(
                    dailyClassType,
                    index == 1 || index == 3,
                ) // "수업", "시험" selected
            },
            questions = getQuestionsForActivities(
                listOf(
                    "수업",
                    "시험",
                ),
                "데이터구조",
            ),
        )
}
