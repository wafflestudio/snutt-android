package com.wafflestudio.snutt2.lib.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.lib.network.dto.core.ColorDto

@JsonClass(generateAdapter = true)
data class GetColorListResults(
    @param:Json(name = "message") val message: String,
    @param:Json(name = "colors") val colors: List<ColorDto>,
    @param:Json(name = "names") val names: List<String>,
)
