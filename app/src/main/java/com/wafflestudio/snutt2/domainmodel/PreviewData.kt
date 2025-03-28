package com.wafflestudio.snutt2.domainmodel

import com.wafflestudio.snutt2.lib.network.dto.core.NotificationDto

object PreviewData {
    private val sampleNotificationDtos = listOf(
        NotificationDto(
            id = "67d008df6fb110276f3ed659",
            title = "벌써 개강 2주차! 망한 시간표 공유하고 기프티콘 받아가세요",
            message = "와플스튜디오 인스타그램(@wafflestudio_official)에서 망한 시간표 대회 이벤트를 확인해보세요. (참고: [친구 > 닉네임으로 친구 추가])",
            type = 0,
            deeplink = "https://www.instagram.com/p/DG-192cTNfF",
            createdAt = "2025-03-11T09:56:47.729Z",
        ),
        NotificationDto(
            id = "67d008df6fb110276f3ed659",
            title = "벌써 개강 2주차! 망한 시간표 공유하고 기프티콘 받아가세요",
            message = "와플스튜디오 인스타그램(@wafflestudio_official)에서 망한 시간표 대회 이벤트를 확인해보세요. (참고: [친구 > 닉네임으로 친구 추가])",
            type = 1,
            deeplink = "https://www.instagram.com/p/DG-192cTNfF",
            createdAt = "2025-03-11T09:56:47.72Z",
        ),
        NotificationDto(
            id = "67d008df6fb110276f3ed659",
            title = "벌써 개강 2주차! 망한 시간표 공유하고 기프티콘 받아가세요",
            message = "와플스튜디오 인스타그램(@wafflestudio_official)에서 망한 시간표 대회 이벤트를 확인해보세요. (참고: [친구 > 닉네임으로 친구 추가])",
            type = 2,
            deeplink = "https://www.instagram.com/p/DG-192cTNfF",
            createdAt = "2025-03-11T09:56:47.7Z",
        ),
        NotificationDto(
            id = "67d008df6fb110276f3ed659",
            title = "벌써 개강 2주차! 망한 시간표 공유하고 기프티콘 받아가세요",
            message = "와플스튜디오 인스타그램(@wafflestudio_official)에서 망한 시간표 대회 이벤트를 확인해보세요. (참고: [친구 > 닉네임으로 친구 추가])",
            type = 3,
            deeplink = "https://www.instagram.com/p/DG-192cTNfF",
            createdAt = "2025-03-11T09:56:47Z",
        ),
    )

    val sampleNotifications = sampleNotificationDtos.map { it.domainModel() }
}
