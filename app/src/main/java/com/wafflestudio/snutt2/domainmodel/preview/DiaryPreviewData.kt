package com.wafflestudio.snutt2.domainmodel.preview

import com.wafflestudio.snutt2.domainmodel.DiaryWrite
import com.wafflestudio.snutt2.domainmodel.DiaryWriteQuestion

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
        null, null, null,
    )
}
