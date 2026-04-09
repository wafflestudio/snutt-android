package com.wafflestudio.snutt2.domain.model

data class PushPreferences(
    val lectureUpdate: Boolean,
    val vacancyNotification: Boolean,
    val lectureDiary: Boolean,
)
