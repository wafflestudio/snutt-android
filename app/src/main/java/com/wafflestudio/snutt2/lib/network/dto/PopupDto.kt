package com.wafflestudio.snutt2.lib.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PopupDto(
    @Json(name = "key") val key: String,
    @Json(name = "image_url") val url: String,
    @Json(name = "hidden_days") val popUpHideDays: Int?
)
