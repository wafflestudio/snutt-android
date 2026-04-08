package com.wafflestudio.snutt2.domainmodel

data class PushPreferences(
    val lectureUpdate: Boolean,
    val vacancyNotification: Boolean,
    val lectureDiary: Boolean,
)
