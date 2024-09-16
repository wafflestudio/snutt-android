package com.wafflestudio.snutt2.core.network.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchTimeDto(
    val day: Int,
    val startMinute: Int,
    val endMinute: Int,
)
