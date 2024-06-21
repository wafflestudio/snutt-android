package com.wafflestudio.snutt2.core.network.model

import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.lib.network.dto.core.ColorDto

@JsonClass(generateAdapter = true)
data class PatchThemeParams(
    val name: String,
    val colors: List<ColorDto>,
)
