package com.wafflestudio.snutt2.core.database.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Color(
    @Json(name = "fg") val fgRaw: String? = null,
    @Json(name = "bg") val bgRaw: String? = null,
)
