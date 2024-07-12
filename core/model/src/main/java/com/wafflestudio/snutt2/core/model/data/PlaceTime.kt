package com.wafflestudio.snutt2.core.model.data

data class PlaceTime(
    val timetableBlock: TimetableBlock,
    val place: Place,
)

data class TimetableBlock (
    val day: Day,
    val startTime: Time,
    val endTime: Time,
)

enum class Day(fromInt: Int) {
    MONDAY(0),
    TUESDAY(1),
    WEDNESDAY(2),
    THURSDAY(3),
    FRIDAY(4),
    SATURDAY(5),
    SUNDAY(6),
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

data class Place(
    val name: String,
    val building: Building?
)