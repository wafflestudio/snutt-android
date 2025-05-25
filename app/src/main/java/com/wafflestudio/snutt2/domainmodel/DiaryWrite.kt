package com.wafflestudio.snutt2.domainmodel

data class DiaryWriteQuestion(
    val question: String,
    val options: List<String>,
)

data class DiaryWrite(
    val lectureName: String,
    val todayOptions: List<String> =
        listOf("개강", "수업", "실기", "시험", "발표", "휴강", "종강", "드랍"),
    val questions: List<DiaryWriteQuestion>,
    val moreText: String = "",
)
