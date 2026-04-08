package com.wafflestudio.snutt2.lib.network.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchTimeDto(
    val day: Int,
    val startMinute: Int,
    val endMinute: Int,
)
