package com.wafflestudio.snutt2.lib.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PostAccessTokenByAuthCodeResults(
    @param:Json(name = "access_token") val accessToken: String?,
    @param:Json(name = "expires_in") val expiresIn: Int?,
    @param:Json(name = "scope") val scope: String?,
    @param:Json(name = "token_type") val tokenType: String?,
    @param:Json(name = "id_token") val idToken: String?,
    @param:Json(name = "error") val error: String?,
    @param:Json(name = "error_description") val errorDescription: String?,
)
