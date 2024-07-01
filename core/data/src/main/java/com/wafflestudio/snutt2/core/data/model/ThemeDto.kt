package com.wafflestudio.snutt2.core.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ThemeDtoT(
    val id: String? = null,
    val theme: Int = 0,
    val name: String = "",
    val colors: List<com.wafflestudio.snutt2.core.data.model.ColorDtoT> = emptyList(),
    val isCustom: Boolean = false,
)
