package com.wafflestudio.snutt2.lib.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.domainmodel.BuiltInTheme

// 신규
@JsonClass(generateAdapter = true)
data class TimetableDto(
    @param:Json(name = "id") val id: String?,
    @param:Json(name = "userId") val userId: String,
    @param:Json(name = "year") val year: Int,
    @param:Json(name = "semester") val semester: String,
    @param:Json(name = "lectures") val lectures: List<TimetableLectureDto>,
    @param:Json(name = "title") val title: String,
    @param:Json(name = "theme") val theme: String,
    @param:Json(name = "themeId") val themeId: String?,
    @param:Json(name = "isPrimary") val isPrimary: Boolean,
    @param:Json(name = "updatedAt") val updatedAt: String,
) : Comparable<TimetableDto> {

    override fun compareTo(other: TimetableDto): Int {
        if (year > other.year) return -1
        if (year < other.year) return 1
        if (year == other.year) {
            val thisSemester = semester.toIntOrNull() ?: 0
            val otherSemester = other.semester.toIntOrNull() ?: 0
            if (thisSemester > otherSemester) return -1
            if (thisSemester < otherSemester) return 1
            if (thisSemester == otherSemester) {
                // update time 기준으로 비교!
                return 0
            }
        }
        return 0
    }

    companion object {
        val Default = TimetableDto(
            id = "",
            userId = "",
            year = 2022,
            semester = "1",
            title = "나의 시간표",
            lectures = emptyList(),
            updatedAt = "default",
            theme = BuiltInTheme.SNUTT.code.toString(),
            themeId = null,
            isPrimary = false,
        )
    }
}
