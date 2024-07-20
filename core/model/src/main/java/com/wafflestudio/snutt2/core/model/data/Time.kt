package com.wafflestudio.snutt2.core.model.data

data class Time(
    val timeInMinutes: Int
) : Comparable<Time> {
    val minute: Int get() = timeInMinutes % 60
    val hour: Int get() = timeInMinutes / 60

    override fun compareTo(other: Time): Int = timeInMinutes.compareTo(other.timeInMinutes)

    companion object {
        fun fromHour(timeInHours: Int): Time = Time(
            timeInHours * 60
        )
    }
}
