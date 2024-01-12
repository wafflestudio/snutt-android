package com.wafflestudio.snutt2.lib.network.dto.core

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.model.BuiltInTheme

@JsonClass(generateAdapter = true)
data class TableDto(
    @Json(name = "_id") val id: String,
    @Json(name = "year") val year: Long,
    @Json(name = "semester") val semester: Long,
    @Json(name = "title") val title: String,
    @Json(name = "lecture_list") val lectureList: List<LectureDto> = emptyList(),
    @Json(name = "updated_at") val updatedAt: String,
    @Json(name = "total_credit") val totalCredit: Long?,
    @Json(name = "theme") val theme: BuiltInTheme,
    @Json(name = "themeId") val themeId: String? = null,
    @Json(name = "isPrimary") val isPrimary: Boolean = false,
) : Comparable<TableDto> {

    override fun compareTo(other: TableDto): Int {
        if (year > other.year) return -1
        if (year < other.year) return 1
        if (year == other.year) {
            if (semester > other.semester) return -1
            if (semester < other.semester) return 1
            if (semester == other.semester) {
                // update time 기준으로 비교!
                return 0
            }
        }
        return 0
    }

    companion object {
        val Default = TableDto(
            id = "",
            year = 2022,
            semester = 1,
            title = "나의 시간표",
            lectureList = emptyList(),
            updatedAt = "default",
            totalCredit = 0,
            theme = BuiltInTheme.SNUTT,
        )
    }
}
