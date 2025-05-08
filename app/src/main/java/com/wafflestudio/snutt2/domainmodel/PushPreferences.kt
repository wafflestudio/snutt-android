package com.wafflestudio.snutt2.domainmodel

data class PushPreferences(
    val lectureUpdate: Boolean,
    val vacancyNotification: Boolean,
)

enum class PushPreferenceType {
    LECTURE_UPDATE, VACANCY_NOTIFICATION
}
