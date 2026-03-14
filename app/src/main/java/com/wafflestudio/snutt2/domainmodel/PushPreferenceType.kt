package com.wafflestudio.snutt2.domainmodel

enum class PushPreferenceType {
    LECTURE_UPDATE, VACANCY_NOTIFICATION, DIARY
}

fun PushPreferenceType.getString(): String {
    return when (this) {
        PushPreferenceType.LECTURE_UPDATE -> "LECTURE_UPDATE"
        PushPreferenceType.VACANCY_NOTIFICATION -> "VACANCY_NOTIFICATION"
        PushPreferenceType.DIARY -> "DIARY"
    }
}
