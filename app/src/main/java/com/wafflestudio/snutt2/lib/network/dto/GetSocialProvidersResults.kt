package com.wafflestudio.snutt2.lib.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetSocialProvidersResults(
    @Json(name = "local") val local: Boolean,
    @Json(name = "facebook") val facebook: Boolean,
    @Json(name = "google") val google: Boolean,
    @Json(name = "kakao") val kakao: Boolean,
    @Json(name = "apple") val apple: Boolean,
)
