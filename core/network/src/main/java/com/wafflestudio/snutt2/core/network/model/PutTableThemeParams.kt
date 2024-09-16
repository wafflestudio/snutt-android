package com.wafflestudio.snutt2.core.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PutTableThemeParams(
    @Json(name = "theme") val theme: Int? = null,
    @Json(name = "themeId") val themeId: String? = null,
)
