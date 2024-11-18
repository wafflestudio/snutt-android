package com.wafflestudio.snutt2.domain_model

import com.wafflestudio.snutt2.lib.network.dto.core.NotificationDto

data class Notification(
    val title: String,
    val message: String,
    val createdAt: String,
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

fun NotificationDto.domainModel() = Notification(
    title = title,
    message = message,
    createdAt = createdAt,
    type = when (type) {
        0 -> NotificationType.Warning
        1 -> NotificationType.Calendar
        2 -> NotificationType.RefreshTime
        3 -> NotificationType.Trash
        4 -> NotificationType.Vacancy
        5 -> NotificationType.Friend
        else -> NotificationType.Megaphone
    },
    deeplink = deeplink,
)
