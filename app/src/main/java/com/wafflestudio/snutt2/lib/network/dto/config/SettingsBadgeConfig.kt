package com.wafflestudio.snutt2.lib.network.dto.config

import com.squareup.moshi.Json

data class SettingsBadgeConfig(
    @Json(name = "new") val new: List<String> = emptyList()
)
