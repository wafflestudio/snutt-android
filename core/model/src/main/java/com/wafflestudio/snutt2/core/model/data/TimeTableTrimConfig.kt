package com.wafflestudio.snutt2.core.model.data

data class TimeTableTrimConfig(
    val startDay: Day,
    val endDay: Day,
    val startTime: Time,
    val endTime: Time,
    val forceFitLectures: Boolean,
)