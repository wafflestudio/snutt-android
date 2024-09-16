package com.wafflestudio.snutt2.core.network.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ThemeDto(
    val id: String? = null,
    val theme: Int = 0,
    val name: String = "",
    val colors: List<ColorDto> = emptyList(),
    val isCustom: Boolean = false,
)
