package com.wafflestudio.snutt2.core.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchTimeDtoT(
    val day: Int,
    val startMinute: Int,
    val endMinute: Int,
)
