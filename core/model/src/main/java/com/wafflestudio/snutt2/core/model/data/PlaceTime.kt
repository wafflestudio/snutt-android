package com.wafflestudio.snutt2.core.model.data

import kotlin.math.max
import kotlin.math.min

data class PlaceTime(
    val timetableBlock: TimetableBlock,
    val placeName: String,
)

data class TimetableBlock (
    val day: Day,
    val startTime: Time,
    val endTime: Time,
){
    fun trimByTrimParam(tableTrimParam: TimeTableTrimConfig): TimetableBlock? {
        if (tableTrimParam.startDay > day || day > tableTrimParam.endDay) return null
        if (tableTrimParam.startTime >= endTime || tableTrimParam.endTime <= endTime) return null

        return this.copy(
            day = day,
            startTime = if (startTime > tableTrimParam.startTime) startTime else tableTrimParam.startTime,
            endTime = if (endTime < tableTrimParam.endTime) endTime else tableTrimParam.endTime,
        )
    }
}

enum class Day(order: Int) {
    MONDAY(0),
    TUESDAY(1),
    WEDNESDAY(2),
    THURSDAY(3),
    FRIDAY(4),
    SATURDAY(5),
    SUNDAY(6);
}

data class Time(
    val timeInMinutes: Int
) : Comparable<Time> {
    companion object {
        fun fromHour(timeInHours: Int): Time = Time(
            timeInHours * 60
        )
    }

    val minute: Int get() = timeInMinutes % 60
    val hour: Int get() = timeInMinutes / 60

    override fun compareTo(other: Time): Int {
        return this.timeInMinutes - other.timeInMinutes
    }
}
