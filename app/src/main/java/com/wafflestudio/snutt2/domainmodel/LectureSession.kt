package com.wafflestudio.snutt2.domainmodel

import java.time.DayOfWeek
import java.time.LocalTime

data class LectureSession(
    val id: String?,
    val day: DayOfWeek,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val place: String,
)
