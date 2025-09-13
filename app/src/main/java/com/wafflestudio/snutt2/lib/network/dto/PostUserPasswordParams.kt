package com.wafflestudio.snutt2.lib.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PostUserPasswordParams(
    @param:Json(name = "id") val id: String,
    @param:Json(name = "password") val password: String,
)
