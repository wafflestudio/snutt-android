package com.wafflestudio.snutt2.lib.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CourseBookDto(
    @param:Json(name = "semester") val semester: Long,
    @param:Json(name = "year") val year: Long,
) : Comparable<CourseBookDto> {
    override fun compareTo(other: CourseBookDto): Int {
        if (year > other.year) {
            return -1
        } else if (year < other.year) {
            return 1
        } else {
            if (semester > other.semester) {
                return -1
            } else if (semester < other.semester) return 1
        }
        return 0
    }
}
