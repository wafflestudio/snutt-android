package com.wafflestudio.snutt2.core.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.lib.network.dto.core.ColorDto

@JsonClass(generateAdapter = true)
data class GetColorListResults(
    @Json(name = "message") val message: String,
    @Json(name = "colors") val colors: List<ColorDto>,
    @Json(name = "names") val names: List<String>,
)

// TODO : 얘는 안쓰고 있음
