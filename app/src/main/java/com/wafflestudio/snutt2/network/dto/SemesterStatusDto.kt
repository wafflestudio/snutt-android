package com.wafflestudio.snutt2.network.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SemesterStatusDto(
    val current: CourseBookDto?,
    val next: CourseBookDto,
) {
    companion object {
        val Default = SemesterStatusDto(
            current = null,
            next = CourseBookDto(semester = 0L, year = 0L),
        )
    }
}
