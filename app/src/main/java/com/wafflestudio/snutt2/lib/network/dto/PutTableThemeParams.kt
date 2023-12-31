package com.wafflestudio.snutt2.lib.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.lib.network.dto.core.ThemeDto

@JsonClass(generateAdapter = true)
data class PutTableThemeParams(
    @Json(name = "theme") val theme: ThemeDto,
)
