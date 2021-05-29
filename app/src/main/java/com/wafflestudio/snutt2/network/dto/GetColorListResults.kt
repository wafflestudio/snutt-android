package com.wafflestudio.snutt2.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.network.dto.core.ColorDto

@JsonClass(generateAdapter = true)
data class GetColorListResults(
    @Json(name = "message") val message: String,
    @Json(name = "colors") val colors: List<ColorDto>,
    @Json(name = "names") val names: List<String>
)
