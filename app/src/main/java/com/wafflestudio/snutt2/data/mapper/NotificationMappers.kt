package com.wafflestudio.snutt2.data.mapper

import com.wafflestudio.snutt2.domain.model.Notification
import com.wafflestudio.snutt2.domain.model.NotificationType
import com.wafflestudio.snutt2.network.dto.NotificationDto
import com.wafflestudio.snutt2.ui.util.SNUTTStringUtils

fun NotificationDto.toDomain(): Notification = Notification(
    title = title,
    message = message,
    createdAt = SNUTTStringUtils.getLocalDateTimeFromString(createdAt),
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
