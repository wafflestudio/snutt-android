package com.wafflestudio.snutt2.data.mapper

import com.wafflestudio.snutt2.domain.model.SemesterStatus
import com.wafflestudio.snutt2.network.dto.SemesterStatusDto

fun SemesterStatusDto.toDomain(): SemesterStatus = SemesterStatus(
    current = current?.toDomain(),
    next = next.toDomain(),
)
