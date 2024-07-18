package com.wafflestudio.snutt2.lib.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PostAccessTokenByAuthCodeResults(
    @Json(name = "access_token") val accessToken: String?,
    @Json(name = "expires_in") val expiresIn: Int?,
    @Json(name = "scope") val scope: String?,
    @Json(name = "token_type") val tokenType: String?,
    @Json(name = "id_token") val idToken: String?,
    @Json(name = "error") val error: String?,
    @Json(name = "error_description") val errorDescription: String?,
)
