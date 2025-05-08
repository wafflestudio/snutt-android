package com.wafflestudio.snutt2.domainmodel

import com.wafflestudio.snutt2.lib.network.dto.PushPreferenceDto
import com.wafflestudio.snutt2.lib.network.dto.PushPreferenceItemDto

data class PushPreferences(
    val lectureUpdate: Boolean,
    val vacancyNotification: Boolean,
)

fun PushPreferences.toNetworkModel(): PushPreferenceDto {
    return PushPreferenceDto(
        pushPreferences = listOf(
            PushPreferenceItemDto(type = PushPreferenceType.LECTURE_UPDATE.getString(), isEnabled = lectureUpdate),
            PushPreferenceItemDto(type = PushPreferenceType.VACANCY_NOTIFICATION.getString(), isEnabled = vacancyNotification),
        ),
    )
}
