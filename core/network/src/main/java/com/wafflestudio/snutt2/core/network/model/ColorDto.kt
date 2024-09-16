package com.wafflestudio.snutt2.core.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ColorDto(
    @Json(name = "fg") val fgRaw: String? = null,
    @Json(name = "bg") val bgRaw: String? = null,
)
