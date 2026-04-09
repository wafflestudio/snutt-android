package com.wafflestudio.snutt2.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SimpleTableDto(
    @param:Json(name = "_id") val id: String,
    @param:Json(name = "year") val year: Long,
    @param:Json(name = "semester") val semester: Long,
    @param:Json(name = "title") val title: String,
    @param:Json(name = "updated_at") val updatedAt: String,
    @param:Json(name = "total_credit") val totalCredit: Long?,
    @param:Json(name = "isPrimary") val isPrimary: Boolean = false,
) {
    companion object {
        val Default = SimpleTableDto(
            id = "", year = 2022, semester = 1L, title = "", updatedAt = "", totalCredit = null,
        )
    }
}
