package com.wafflestudio.snutt2.lib.network.dto

import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.lib.network.dto.core.ColorDto

@JsonClass(generateAdapter = true)
data class PostThemeParams(
    val name: String,
    val colors: List<ColorDto>,
)
