package com.wafflestudio.snutt2.model

import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.domainmodel.SemesterStatus
import com.wafflestudio.snutt2.lib.network.dto.core.CourseBookDto
import com.wafflestudio.snutt2.lib.network.dto.core.toDomainModel

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

fun SemesterStatusDto.toDomainModel(): SemesterStatus {
    return SemesterStatus(
        current = current?.toDomainModel(),
        next = next.toDomainModel(),
    )
}
