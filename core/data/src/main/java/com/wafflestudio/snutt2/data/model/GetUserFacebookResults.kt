package com.wafflestudio.snutt2.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetUserFacebookResultsT(
    @Json(name = "name") val name: String,
    @Json(name = "attached") val attached: Boolean,
)
