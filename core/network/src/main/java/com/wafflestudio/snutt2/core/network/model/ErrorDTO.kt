package com.wafflestudio.snutt2.core.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ErrorDTO(
    @Json(name = "errcode") val code: Int? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "displayMessage") val displayMessage: String? = null,
    @Json(name = "ext") val ext: Map<String, String>? = null,
    @Json(name = "detail") val detail: ErrorDetail? = null,
) {
    data class ErrorDetail(
        @Json(name = "socialProvider") val socialProvider: String? = null,
    )
}
