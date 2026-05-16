package com.wafflestudio.snutt2.storage.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ColorLocalEntity(
    @param:Json(name = "fg") val fgRaw: String? = null,
    @param:Json(name = "bg") val bgRaw: String? = null,
)
