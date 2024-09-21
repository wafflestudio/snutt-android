package com.wafflestudio.snutt2.core.database.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// TODO: naming 변경 제안
@JsonClass(generateAdapter = true)
data class Table(
    @Json(name = "_id") val id: String,
    @Json(name = "year") val year: Long,
    @Json(name = "semester") val semester: Long,
    @Json(name = "title") val title: String,
    @Json(name = "lecture_list") val lectureList: List<Lecture> = emptyList(),
    @Json(name = "updated_at") val updatedAt: String,
    @Json(name = "total_credit") val totalCredit: Long?,
    @Json(name = "theme") val theme: Int,
    @Json(name = "themeId") val themeId: String? = null,
    @Json(name = "isPrimary") val isPrimary: Boolean = false,
)
