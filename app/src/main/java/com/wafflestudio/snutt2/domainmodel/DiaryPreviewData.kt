package com.wafflestudio.snutt2.domainmodel

import com.wafflestudio.snutt2.lib.Selectable

object DiaryPreviewData {

    val diaryWritePreviewData =
        DiaryWrite(
            lectureName = "시각디자인기초",
            questions = listOf(
                DiaryWriteQuestion(
                    question = "수강신청은 어땠나요?",
                    options = listOf(
                        Selectable("널널했어요", false),
                        Selectable("1픽 했어요", false),
                        Selectable("2~3픽 했어요", false),
                        Selectable("초안지 썼어요", false),
                    ),
                ),
                DiaryWriteQuestion(
                    question = "교수님의 첫인상은 어땠나요?",
                    options = listOf(
                        Selectable("두려워요", false),
                        Selectable("두려워요", false),
                        Selectable("유익했어요", false),
                        Selectable("유익했어요", false),
                    ),
                ),
                DiaryWriteQuestion(
                    question = "수업 끝까지 들을 것 같나요?",
                    options = listOf(
                        Selectable("끝까지 들을 거에요", false),
                        Selectable("모르겠어요", false),
                        Selectable("드랍할 것 같아요", false),
                    ),
                ),
            ),
        )
}
