package com.wafflestudio.snutt2.network.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PostThemeParams(
    val name: String,
    val colors: List<ColorDto>,
)
