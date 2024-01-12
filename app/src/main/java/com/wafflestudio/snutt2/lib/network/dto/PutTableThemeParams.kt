package com.wafflestudio.snutt2.lib.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.model.BuiltInTheme

@JsonClass(generateAdapter = true)
data class PutTableThemeParams(
    @Json(name = "theme") val theme: BuiltInTheme? = null,
    @Json(name = "themeId") val themeId: String? = null,
)
