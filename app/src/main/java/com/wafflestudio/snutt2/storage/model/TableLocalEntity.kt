package com.wafflestudio.snutt2.storage.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TableLocalEntity(
    @param:Json(name = "_id") val id: String,
    @param:Json(name = "year") val year: Long,
    @param:Json(name = "semester") val semester: Long,
    @param:Json(name = "title") val title: String,
    @param:Json(name = "lecture_list") val lectureList: List<LectureLocalEntity> = emptyList(),
    @param:Json(name = "updated_at") val updatedAt: String,
    @param:Json(name = "total_credit") val totalCredit: Long?,
    @param:Json(name = "theme") val theme: Int,
    @param:Json(name = "themeId") val themeId: String? = null,
    @param:Json(name = "isPrimary") val isPrimary: Boolean = false,
)
