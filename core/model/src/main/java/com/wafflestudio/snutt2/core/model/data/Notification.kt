package com.wafflestudio.snutt2.core.model.data

import java.time.LocalDateTime

data class Notification(
    val title: String,
    val message: String,
    val createdAt: LocalDateTime,
    val type: Type,
    val deeplink: String?,
) {
    enum class Type {
        Normal,
        NewCourseBook,
        LectureUpdated,
        LectureDeleted,
        Vacancy,
        Friend,
        Fallback
    }
}