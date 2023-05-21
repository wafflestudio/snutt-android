package com.wafflestudio.snutt2.lib.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetShareTableLinkResults(
    @Json(name = "shortLink") val shortLink: String,
    @Json(name = "previewLink") val previewLink: String,
)
