package com.wafflestudio.snutt2.domain.model

import java.time.LocalDateTime

data class Notification(
    val title: String,
    val message: String,
    val createdAt: LocalDateTime,
    val type: NotificationType,
    val deeplink: String?,
)

enum class NotificationType {
    Warning,
    Calendar,
    RefreshTime,
    Trash,
    Vacancy,
    Friend,
    Megaphone,
}
