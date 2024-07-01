package com.wafflestudio.snutt2.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ColorDtoT(
    @Json(name = "fg") val fgRaw: String? = null,
    @Json(name = "bg") val bgRaw: String? = null,
)
