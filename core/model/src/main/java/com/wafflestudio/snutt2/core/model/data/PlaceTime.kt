package com.wafflestudio.snutt2.core.model.data

data class PlaceTime(
    val timetableBlock: TimetableBlock,
    val placeName: String,
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
    SUNDAY,
}

data class Time (
    val timeInMinutes: Int
) {
    companion object {
        fun fromHour(timeInHours: Int): Time = Time(
            timeInHours * 60
        )
    }

    val minute: Int get() = timeInMinutes % 60
    val hour: Int get() = timeInMinutes / 60
}
