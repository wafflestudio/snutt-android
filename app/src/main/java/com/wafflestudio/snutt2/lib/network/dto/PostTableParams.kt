package com.wafflestudio.snutt2.lib.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PostTableParams(
    @Json(name = "year") val year: Long,
    @Json(name = "semester") val semester: Long,
    @Json(name = "title") val title: String? = null,
)
