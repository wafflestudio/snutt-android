package com.wafflestudio.snutt2.lib.network.dto.core

import com.squareup.moshi.Json

data class SettingsBadgeConfig(
    @Json(name = "new") val new: List<String> = emptyList()
)
