package com.wafflestudio.snutt2.lib

import com.wafflestudio.snutt2.lib.network.dto.core.ClassTimeDto

data class Time12(
    var amPm: Int,
    var hour: Int,
    var minute: Int,
) : Comparable<Time12> {

    override fun equals(other: Any?): Boolean {
        return this.hashCode() == other.hashCode()
    }

    override fun hashCode(): Int {
        return (amPm * 12 + hour) * 60 + minute
    }

    override fun compareTo(other: Time12): Int {
        return if (this.hashCode() > other.hashCode()) 1
        else if (this.hashCode() == other.hashCode()) 0
        else -1
    }

    fun isFirst(): Boolean {
        return this.amPm == 0 && this.hour == 0 && this.minute == 0
    }

    fun isLast(): Boolean {
        return this.amPm == 1 && this.hour == 11 && this.minute == 55
    }

    /*
     * 예시: 오전 9:30, 오후 11:05, 오전 12:00 (자정), 오후 12:00 (정오)
    */
    override fun toString(): String {
        return StringBuilder()
            .append(if (amPm == 0) "오전 " else "오후 ")
            .append(if (hour == 0) 12 else hour)
            .append(":")
            .append("%02d".format(minute))
            .toString()
    }

    /*
     * 예시: 9:30, 23:05, 0:00 (자정), 12:00 (정오)
    */
    fun toString24(): String {
        return StringBuilder()
            .append(amPm * 12 + hour)
            .append(":")
            .append("%02d".format(minute))
            .toString()
    }
}

fun Time12.next(): Time12 {
    val time = (this.amPm * 12 + this.hour) * 60 + this.minute + 5
    return Time12(
        time / 720,
        (time % 720) / 60,
        time % 60
    )
}

fun Time12.prev(): Time12 {
    val time = (this.amPm * 12 + this.hour) * 60 + this.minute - 5
    return Time12(
        time / 720,
        (time % 720) / 60,
        time % 60
    )
}

fun ClassTimeDto.startTime12(): Time12 {
    return Time12(
        this.startTimeHour / 12,
        this.startTimeHour % 12,
        this.startTimeMinute
    )
}
fun ClassTimeDto.endTime12(): Time12 {
    return Time12(
        this.endTimeHour / 12,
        this.endTimeHour % 12,
        this.endTimeMinute
    )
}
