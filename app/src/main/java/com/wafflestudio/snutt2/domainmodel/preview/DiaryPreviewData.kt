package com.wafflestudio.snutt2.domainmodel.preview

import com.wafflestudio.snutt2.domainmodel.DiaryWrite
import com.wafflestudio.snutt2.domainmodel.DiaryWriteQuestion

object DiaryPreviewData {

    val diaryWritePreviewData =
        DiaryWrite(
            lectureName = "시각디자인기초",
            questions = listOf(
                DiaryWriteQuestion(
                    question = "수강신청은 어땠나요?",
                    options = listOf("널널했어요", "1픽 했어요", "2~3픽 했어요", "초안지 썼어요"),
                ),
                DiaryWriteQuestion(
                    question = "교수님의 첫인상은 어땠나요?",
                    options = listOf("두려워요", "두려워요", "유익했어요", "유익했어요"),
                ),
                DiaryWriteQuestion(
                    question = "수업 끝까지 들을 것 같나요?",
                    options = listOf("끝까지 들을 거에요", "모르겠어요", "드랍할 것 같아요"),
                ),
            ),
        )
}
