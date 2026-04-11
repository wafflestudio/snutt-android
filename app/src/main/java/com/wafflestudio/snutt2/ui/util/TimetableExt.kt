package com.wafflestudio.snutt2.ui.util

import com.wafflestudio.snutt2.domain.model.Lecture
import com.wafflestudio.snutt2.domain.model.LectureSession
import com.wafflestudio.snutt2.domain.model.TableTrimParam
import java.time.LocalTime
import kotlin.math.ceil
import kotlin.math.floor
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

fun List<Lecture>.getFittingTrimParam(tableTrimParam: TableTrimParam): TableTrimParam =
    TableTrimParam(
        dayOfWeekFrom = (flatMap { it.lectureSessions.map { it.day.ordinal } } + tableTrimParam.dayOfWeekFrom).minOf { it },
        dayOfWeekTo = (flatMap { it.lectureSessions.map { it.day.ordinal } } + tableTrimParam.dayOfWeekTo).maxOf { it },
        hourFrom = (flatMap { it.lectureSessions.map { floor(it.startTimeInFloat).toInt() } } + tableTrimParam.hourFrom).minOf { it },
        hourTo = (flatMap { it.lectureSessions.map { ceil(it.endTimeInFloat).toInt() - 1 } } + tableTrimParam.hourTo).maxOf { it },
        forceFitLectures = true,
    )

fun Lecture.contains(queryDay: Int, queryTime: Float): Boolean {
    for (session in this.lectureSessions) {
        if (queryDay != session.day.ordinal) continue
        if (queryTime in session.startTimeInFloat..session.endTimeInFloat) return true
    }
    return false
}

fun roundToCompact(f: Float): Float {
    return if (f - f.toInt() == 0f) {
        f
    } else if (f - f.toInt() <= 0.5) {
        f.toInt() + 0.5f
    } else {
        f.toInt() + 1f
    }
}
