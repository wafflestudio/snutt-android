package com.wafflestudio.snutt2.domain.model

import java.time.LocalTime
import kotlin.math.max
import kotlin.math.min

val LectureSession.startTimeInFloat: Float
    get() = startTime.hour + startTime.minute / 60f

val LectureSession.endTimeInFloat: Float
    get() = endTime.hour + endTime.minute / 60f

fun LectureSession.trimByTrimParam(tableTrimParam: TableTrimParam): LectureSession? {
    val dayIdx = day.ordinal
    if (tableTrimParam.dayOfWeekFrom > dayIdx || dayIdx > tableTrimParam.dayOfWeekTo) return null
    if (tableTrimParam.hourFrom >= endTimeInFloat || tableTrimParam.hourTo + 1 <= startTimeInFloat) return null

    val clampedStartMinutes = max(tableTrimParam.hourFrom * 60, startTime.hour * 60 + startTime.minute)
    val clampedEndMinutes = min(endTime.hour * 60 + endTime.minute, (tableTrimParam.hourTo + 1) * 60)

    return this.copy(
        startTime = LocalTime.of(clampedStartMinutes / 60, clampedStartMinutes % 60),
        endTime = LocalTime.of(clampedEndMinutes / 60, clampedEndMinutes % 60),
    )
}
