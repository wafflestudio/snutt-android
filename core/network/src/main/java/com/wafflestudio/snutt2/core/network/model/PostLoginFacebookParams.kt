package com.wafflestudio.snutt2.core.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PostLoginFacebookParams(
    @Json(name = "fb_id") val facebookId: String,
    @Json(name = "fb_token") val facebookToken: String,
)
