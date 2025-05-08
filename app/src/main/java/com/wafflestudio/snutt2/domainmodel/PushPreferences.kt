package com.wafflestudio.snutt2.domainmodel

import com.wafflestudio.snutt2.lib.network.dto.PushPreferenceDto
import com.wafflestudio.snutt2.lib.network.dto.PushPreferenceItemDto

data class PushPreferences(
    val lectureUpdate: Boolean,
    val vacancyNotification: Boolean,
)

enum class PushPreferenceType {
    LECTURE_UPDATE, VACANCY_NOTIFICATION
}

fun PushPreferences.toNetworkModel(): PushPreferenceDto {
    return PushPreferenceDto(
        pushPreferences = listOf(
            PushPreferenceItemDto(type = "LECTURE_UPDATE", isEnabled = lectureUpdate),
            PushPreferenceItemDto(type = "VACANCY_NOTIFICATION", isEnabled = vacancyNotification),
        ),
    )
}
