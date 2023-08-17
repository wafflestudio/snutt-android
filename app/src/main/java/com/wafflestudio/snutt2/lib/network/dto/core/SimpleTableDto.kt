package com.wafflestudio.snutt2.lib.network.dto.core

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SimpleTableDto(
    @Json(name = "_id") val id: String,
    @Json(name = "year") val year: Long,
    @Json(name = "semester") val semester: Long,
    @Json(name = "title") val title: String,
    @Json(name = "updated_at") val updatedAt: String,
    @Json(name = "total_credit") val totalCredit: Long?,
    @Json(name = "is_primary") val isPrimary: Boolean = false,
) {
    companion object {
        val Default = SimpleTableDto(
            id = "", year = 2022, semester = 1L, title = "", updatedAt = "", totalCredit = null,
        )
    }
}
