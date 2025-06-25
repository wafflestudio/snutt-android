package com.wafflestudio.snutt2.domainmodel

data class DiaryWriteQuestion(
    val question: String,
    val options: List<String>,
)

data class DiaryWriteInit(
    val lectureName: String,
    val todayState: List<Boolean>?,
    val questionsState: List<List<Boolean>>?,
    val moreText: String?,
)
