package com.wafflestudio.snutt2.domainmodel

import com.wafflestudio.snutt2.lib.Selectable

data class DiaryWriteQuestion(
    val question: String,
    val options: List<Selectable<String>>,
)

data class DiaryWrite(
    val lectureName: String,
    val todayOptions: List<Selectable<String>> =
        listOf(
            Selectable("개강", true),
            Selectable("수업", false),
            Selectable("실기", false),
            Selectable("시험", false),
            Selectable("발표", false),
            Selectable("휴강", false),
            Selectable("종강", false),
            Selectable("드랍", false),
        ),
    val questions: List<DiaryWriteQuestion>,
    val moreText: String = "",
)
