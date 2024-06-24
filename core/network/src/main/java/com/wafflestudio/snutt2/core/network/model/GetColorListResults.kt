package com.wafflestudio.snutt2.core.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetColorListResults(
    @Json(name = "message") val message: String,
    @Json(name = "colors") val colors: List<ColorDto>,
    @Json(name = "names") val names: List<String>,
)
