package com.wafflestudio.snutt2.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ThemeDtoT(
    val id: String? = null,
    val theme: Int = 0,
    val name: String = "",
    val colors: List<ColorDtoT> = emptyList(),
    val isCustom: Boolean = false,
)
