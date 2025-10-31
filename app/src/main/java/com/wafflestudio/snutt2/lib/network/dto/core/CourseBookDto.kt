package com.wafflestudio.snutt2.lib.network.dto.core

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.domainmodel.CourseBook

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

fun CourseBookDto.toDomainModel(): CourseBook {
    return CourseBook(
        semester = this.semester,
        year = this.year,
    )
}
