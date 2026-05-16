package com.wafflestudio.snutt2.storage.model

import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.domain.model.SemesterStatus

@JsonClass(generateAdapter = true)
data class SemesterStatusLocalEntity(
    val current: CourseBookLocalEntity?,
    val next: CourseBookLocalEntity,
)

fun SemesterStatusLocalEntity.toDomainModel(): SemesterStatus = SemesterStatus(
    current = current?.toDomainModel(),
    next = next.toDomainModel(),
)

fun SemesterStatus.toLocalEntity(): SemesterStatusLocalEntity = SemesterStatusLocalEntity(
    current = current?.toLocalEntity(),
    next = next.toLocalEntity(),
)
