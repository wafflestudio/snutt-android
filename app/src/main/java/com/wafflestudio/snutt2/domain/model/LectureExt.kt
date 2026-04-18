package com.wafflestudio.snutt2.domain.model

import kotlin.math.ceil
import kotlin.math.floor

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

fun getCreditSumFromLectureList(lectureList: List<Lecture>): Long {
    return lectureList.fold(0L) { acc, lecture -> acc + lecture.credit }
}
