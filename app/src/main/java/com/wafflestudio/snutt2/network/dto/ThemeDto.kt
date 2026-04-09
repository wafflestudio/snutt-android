package com.wafflestudio.snutt2.network.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ThemeDto(
    val id: String?,
    val theme: Int?,
    val name: String?,
    val colors: List<ColorDto>?,
    val isCustom: Boolean?,
    val status: String?,
)
