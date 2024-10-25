package com.wafflestudio.snutt2.model

data class TableLectureCustom(
    val title: Boolean,
    val place: Boolean,
    val lectureNumber: Boolean,
    val instructor: Boolean,
) {
    companion object {
        val Default = TableLectureCustom(true, true, false, false)
    }
}

enum class TableLectureCustomOptions {
    TITLE,
    PLACE,
    LECTURENUMBER,
    INSTRUCTOR,
}
