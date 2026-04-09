package com.wafflestudio.snutt2.network.dto

import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.network.dto.ColorDto

@JsonClass(generateAdapter = true)
data class PatchThemeParams(
    val name: String,
    val colors: List<ColorDto>,
)
