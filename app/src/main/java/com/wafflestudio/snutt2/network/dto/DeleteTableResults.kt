package com.wafflestudio.snutt2.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class DeleteTableResults(
    @Json(name = "year") val year: String,
    @Json(name = "semester") val semester: Long,
    @Json(name = "title") val title: String,
    @Json(name = "_id") val id: String,
    @Json(name = "updated_at") val updated_at: String
)
