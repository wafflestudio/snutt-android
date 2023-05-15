package com.wafflestudio.snutt2.lib.network.dto.core

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SimpleSharedTableDto(
    @Json(name = "id") val id: String,
    @Json(name = "title") val title: String,
    @Json(name = "is_valid") val idValid: Boolean,
    @Json(name = "year") val year: Long,
    @Json(name = "semester") val semester: Long,
)
