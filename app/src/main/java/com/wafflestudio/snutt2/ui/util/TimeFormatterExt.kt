package com.wafflestudio.snutt2.ui.util

import android.content.Context
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.domain.model.SearchTime
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun getLocalDateTimeFromString(data: String): LocalDateTime {
    val instant = Instant.parse(data)
    return instant.atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime()
}

fun getNotificationTime(context: Context, dateTime: LocalDateTime): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")
    val now = LocalDateTime.now()
    val duration = Duration.between(dateTime, now)

    val days = duration.toDays()
    val hours = duration.toHours()
    val minutes = duration.toMinutes()

    return when {
        days > 0 -> dateTime.format(formatter)
        hours > 0 -> context.getString(R.string.time_hours_ago, hours)
        minutes > 0 -> context.getString(R.string.time_minutes_ago, minutes)
        else -> context.getString(R.string.time_now)
    }
}

fun Int.toFormattedTimeString(context: Context): String {
    val amPm = if (this < SearchTime.MIDDAY_MINUTE) context.getString(R.string.morning) else context.getString(R.string.afternoon)
    val hour = (this / 60).let {
        if (it != 12) it % 12 else it
    }
    return context.getString(R.string.time_format_am_pm, amPm, hour, this % 60)
}
