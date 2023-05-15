package com.wafflestudio.snutt2.lib.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.lib.network.dto.core.SimpleSharedTableDto

@JsonClass(generateAdapter = true)
data class GetSharedTableListResults(
    @Json(name = "content") val content: List<SimpleSharedTableDto>
)
