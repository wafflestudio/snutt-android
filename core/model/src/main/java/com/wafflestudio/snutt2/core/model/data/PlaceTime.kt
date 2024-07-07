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

enum class Day {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY
}

data class Time (
    val timeInMinutes: Int
) {
    val minute: Int get() = timeInMinutes % 60
    val hour: Int get() = timeInMinutes / 60
}

data class Place(
    val name: String,
    val building: Building
)