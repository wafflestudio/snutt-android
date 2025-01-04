package com.wafflestudio.snutt2.domainmodel

import com.wafflestudio.snutt2.lib.data.SNUTTStringUtils
import com.wafflestudio.snutt2.lib.network.dto.core.NotificationDto
import java.util.Date

data class Notification(
    val title: String,
    val message: String,
    val createdAt: Date,
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
    createdAt = SNUTTStringUtils.getDateFromString(createdAt),
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
