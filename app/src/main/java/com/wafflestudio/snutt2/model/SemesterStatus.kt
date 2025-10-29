package com.wafflestudio.snutt2.model

import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.lib.network.dto.core.CourseBookDto

@JsonClass(generateAdapter = true)
data class SemesterStatus(
    val current: CourseBookDto?,
    val next: CourseBookDto,
) {
    companion object {
        val Default = SemesterStatus(
            current = null,
            next = CourseBookDto(semester = 0L, year = 0L),
        )
    }
}
