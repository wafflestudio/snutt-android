package com.wafflestudio.snutt2.network.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PatchThemeParams(
    val name: String,
    val colors: List<ColorDto>,
)
