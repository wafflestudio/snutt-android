package com.wafflestudio.snutt2.lib.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetUserFacebookResults(
    @param:Json(name = "name") val name: String,
    @param:Json(name = "attached") val attached: Boolean,
)
