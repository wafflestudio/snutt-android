package com.wafflestudio.snutt2.core.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetUserFacebookResults(
    @Json(name = "name") val name: String,
    @Json(name = "attached") val attached: Boolean,
)
