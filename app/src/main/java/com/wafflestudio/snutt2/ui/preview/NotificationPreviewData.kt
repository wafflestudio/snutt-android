package com.wafflestudio.snutt2.ui.preview

import com.wafflestudio.snutt2.domain.model.Notification
import com.wafflestudio.snutt2.domain.model.NotificationType
import java.time.LocalDateTime

object NotificationPreviewData {
    private val sampleNotificationTitle = "벌써 개강 2주차! 망한 시간표 공유하고 기프티콘 받아가세요"
    private val sampleNotificationMessage =
        "와플스튜디오 인스타그램(@wafflestudio_official)에서 망한 시간표 대회 이벤트를 확인해보세요. (참고: [친구 > 닉네임으로 친구 추가])"
    private val sampleNotificationDeeplink = "https://www.instagram.com/p/DG-192cTNfF"
    private val sampleNotificationCreatedAt = LocalDateTime.of(2025, 3, 11, 18, 56, 47)

    val sampleNotifications: List<Notification> = listOf(
        NotificationType.Warning,
        NotificationType.Calendar,
        NotificationType.RefreshTime,
        NotificationType.Trash,
    ).map { type ->
        Notification(
            title = sampleNotificationTitle,
            message = sampleNotificationMessage,
            createdAt = sampleNotificationCreatedAt,
            type = type,
            deeplink = sampleNotificationDeeplink,
        )
    }
}
