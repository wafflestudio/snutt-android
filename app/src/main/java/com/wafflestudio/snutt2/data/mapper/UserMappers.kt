package com.wafflestudio.snutt2.data.mapper

import com.wafflestudio.snutt2.domainmodel.Nickname
import com.wafflestudio.snutt2.domainmodel.PushPreferences
import com.wafflestudio.snutt2.domainmodel.PushPreferenceType
import com.wafflestudio.snutt2.domainmodel.User
import com.wafflestudio.snutt2.domainmodel.getString
import com.wafflestudio.snutt2.lib.network.dto.PushPreferenceDto
import com.wafflestudio.snutt2.lib.network.dto.PushPreferenceItemDto
import com.wafflestudio.snutt2.lib.network.dto.NicknameDto
import com.wafflestudio.snutt2.lib.network.dto.UserDto

fun UserDto.toDomain(): User = User(
    email = email,
    localId = localId,
    nickname = nickname?.let { Nickname(nickname = it.nickname, tag = it.tag) },
)

fun NicknameDto.toDomain(): Nickname = Nickname(nickname, tag)

fun PushPreferenceDto.toDomain(): PushPreferences {
    val map = pushPreferences.associateBy { it.type }
    return PushPreferences(
        lectureUpdate = map["LECTURE_UPDATE"]?.isEnabled ?: true,
        vacancyNotification = map["VACANCY_NOTIFICATION"]?.isEnabled ?: true,
        lectureDiary = map["DIARY"]?.isEnabled ?: true,
    )
}

fun PushPreferences.toDto(): PushPreferenceDto = PushPreferenceDto(
    pushPreferences = listOf(
        PushPreferenceItemDto(
            type = PushPreferenceType.LECTURE_UPDATE.getString(),
            isEnabled = lectureUpdate,
        ),
        PushPreferenceItemDto(
            type = PushPreferenceType.VACANCY_NOTIFICATION.getString(),
            isEnabled = vacancyNotification,
        ),
        PushPreferenceItemDto(
            type = PushPreferenceType.DIARY.getString(),
            isEnabled = lectureDiary,
        ),
    ),
)
