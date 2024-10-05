package com.wafflestudio.snutt2.core.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SocialProvidersCheck(
    @Json(name = "local") val local: Boolean,
    @Json(name = "facebook") val facebook: Boolean,
    @Json(name = "google") val google: Boolean,
    @Json(name = "kakao") val kakao: Boolean,
    @Json(name = "apple") val apple: Boolean,
)
