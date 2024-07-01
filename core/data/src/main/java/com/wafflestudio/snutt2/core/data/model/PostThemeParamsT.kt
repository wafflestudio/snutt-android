package com.wafflestudio.snutt2.core.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PostThemeParamsT(
    val name: String,
    val colors: List<ColorDtoT>,
)
