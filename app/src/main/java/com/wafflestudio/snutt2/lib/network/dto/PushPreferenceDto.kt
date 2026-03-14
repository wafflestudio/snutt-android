package com.wafflestudio.snutt2.lib.network.dto

import com.wafflestudio.snutt2.domainmodel.PushPreferences

data class PushPreferenceDto(
    val pushPreferences: List<PushPreferenceItemDto>,
)

fun PushPreferenceDto.toDomainModel(): PushPreferences {
    val map = pushPreferences.associateBy { it.type }

    return PushPreferences(
        lectureUpdate = map["LECTURE_UPDATE"]?.isEnabled ?: true,
        vacancyNotification = map["VACANCY_NOTIFICATION"]?.isEnabled ?: true,
        lectureDiary = map["DIARY"]?.isEnabled ?: true,
    )
}
