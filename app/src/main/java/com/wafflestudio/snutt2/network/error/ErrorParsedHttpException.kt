package com.wafflestudio.snutt2.network.error

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ErrorParsedHttpException(
    @param:Json(name = "errcode") val code: Int,
    override val message: String?,
    val title: String?,
    val displayMessage: String?,
    val ext: Map<String, String>?,
    val displayTitle: String?,
) : Exception(message)
