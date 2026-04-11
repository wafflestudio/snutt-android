package com.wafflestudio.snutt2.domain.model

data class Popup(
    val key: String,
    val imageUri: String,
    val linkUrl: String?,
    val hideDays: Int?,
)
