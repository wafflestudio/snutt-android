package com.wafflestudio.snutt2.lib.network.dto.core

import com.squareup.moshi.Json

data class VacancyBannerConfig(
    @Json(name = "visible") val visible: Boolean = false,
)
