package com.wafflestudio.snutt2.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PutTableThemeParamsT(
    @Json(name = "theme") val theme: Int? = null,
    @Json(name = "themeId") val themeId: String? = null,
)
