package com.wafflestudio.snutt2.core.data.model

import com.wafflestudio.snutt2.core.model.data.Notification
import com.wafflestudio.snutt2.core.network.model.NotificationDto
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun NotificationDto.toExternalModel() = Notification(
    title = this.title,
    message = this.message,
    createdAt = LocalDateTime.parse(this.createdAt, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")),
    type = when(this.type) {
        0 -> Notification.Type.Normal
        1 -> Notification.Type.NewCourseBook
        2 -> Notification.Type.LectureUpdated
        3 -> Notification.Type.LectureDeleted
        4 -> Notification.Type.Vacancy
        5 -> Notification.Type.Friend
        else -> Notification.Type.Fallback
    },
    deeplink = this.deeplink,
)
