package com.wafflestudio.snutt2.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetSocialProvidersResults(
    @param:Json(name = "local") val local: Boolean,
    @param:Json(name = "facebook") val facebook: Boolean,
    @param:Json(name = "google") val google: Boolean,
    @param:Json(name = "kakao") val kakao: Boolean,
    @param:Json(name = "apple") val apple: Boolean,
)
