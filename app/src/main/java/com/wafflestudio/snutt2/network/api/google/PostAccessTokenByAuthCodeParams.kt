package com.wafflestudio.snutt2.network.api.google

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PostAccessTokenByAuthCodeParams(
    @param:Json(name = "code") val authCode: String,
    @param:Json(name = "client_id") val clientId: String,
    @param:Json(name = "client_secret") val clientSecret: String,
    @param:Json(name = "redirect_uri") val redirectUri: String = "",
    @param:Json(name = "grant_type") val grantType: String = "authorization_code",
)
