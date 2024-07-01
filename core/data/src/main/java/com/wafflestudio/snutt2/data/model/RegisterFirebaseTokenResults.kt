package com.wafflestudio.snutt2.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegisterFirebaseTokenResultsT(
    @Json(name = "message") val message: String,
)
