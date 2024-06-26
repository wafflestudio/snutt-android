package com.wafflestudio.snutt2.core.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TableDto(
    @Json(name = "_id") val id: String,
    @Json(name = "year") val year: Long,
    @Json(name = "semester") val semester: Long,
    @Json(name = "title") val title: String,
    @Json(name = "lecture_list") val lectureList: List<LectureDto> = emptyList(),
    @Json(name = "updated_at") val updatedAt: String,
    @Json(name = "total_credit") val totalCredit: Long?,
    @Json(name = "theme") val theme: Int,
    @Json(name = "themeId") val themeId: String? = null,
    @Json(name = "isPrimary") val isPrimary: Boolean = false,
) {
    companion object {
        val Default = TableDto(
            id = "",
            year = 2022,
            semester = 1,
            title = "나의 시간표",
            lectureList = emptyList(),
            updatedAt = "default",
            totalCredit = 0,
            theme = BuiltInTheme.SNUTT.code,
        )
    }
}
