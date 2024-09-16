package com.wafflestudio.snutt2.core.database.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SimpleTable(
    @Json(name = "_id") val id: String,
    @Json(name = "year") val year: Long,
    @Json(name = "semester") val semester: Long,
    @Json(name = "title") val title: String,
    @Json(name = "updated_at") val updatedAt: String,
    @Json(name = "total_credit") val totalCredit: Long?,
    @Json(name = "isPrimary") val isPrimary: Boolean = false,
)
