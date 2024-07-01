package com.wafflestudio.snutt2.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ErrorDTOT(
    @Json(name = "errcode") val code: Int? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "displayMessage") val displayMessage: String? = null,
    @Json(name = "ext") val ext: Map<String, String>? = null,
)
