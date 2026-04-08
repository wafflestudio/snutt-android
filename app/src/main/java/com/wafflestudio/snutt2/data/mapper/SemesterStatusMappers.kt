package com.wafflestudio.snutt2.data.mapper

import com.wafflestudio.snutt2.domainmodel.SemesterStatus
import com.wafflestudio.snutt2.lib.network.dto.SemesterStatusDto

fun SemesterStatusDto.toDomain(): SemesterStatus = SemesterStatus(
    current = current?.toDomain(),
    next = next.toDomain(),
)
