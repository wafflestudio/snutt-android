package com.wafflestudio.snutt2.domainmodel

import java.time.DayOfWeek
import java.time.LocalTime

data class LectureSession(
    val id: String?,
    val day: DayOfWeek,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val place: String,
) {
    companion object {
        val Default = LectureSession(
            id = null,
            day = DayOfWeek.MONDAY,
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(10, 0),
            place = "",
        )
    }
}
