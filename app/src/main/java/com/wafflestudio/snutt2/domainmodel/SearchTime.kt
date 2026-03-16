package com.wafflestudio.snutt2.domainmodel

import java.time.DayOfWeek
import java.time.LocalTime

data class SearchTime(
    val day: DayOfWeek,
    val startTime: LocalTime,
    val endTime: LocalTime,
) {
    companion object {
        val FIRST: LocalTime = LocalTime.MIN
        val MIDDAY: LocalTime = LocalTime.NOON
        val LAST: LocalTime = LocalTime.of(23, 55)

        // UI에서 분(Int) 단위 연산이 필요한 경우 사용
        const val FIRST_MINUTE: Int = 0
        const val MIDDAY_MINUTE: Int = 720
        const val LAST_MINUTE: Int = 1435
    }
}

fun List<LocalLecture>.flatMapToSearchTime(): List<SearchTime> =
    flatMap { it.lectureSessions }.map { session ->
        SearchTime(
            day = session.day,
            startTime = session.startTime,
            endTime = session.endTime,
        )
    }
