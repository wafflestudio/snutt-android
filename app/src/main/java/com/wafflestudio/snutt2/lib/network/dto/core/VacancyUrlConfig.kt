package com.wafflestudio.snutt2.lib.network.dto.core

import com.squareup.moshi.Json

data class VacancyUrlConfig(
    @Json(name = "url") val url: String? = null,
)
