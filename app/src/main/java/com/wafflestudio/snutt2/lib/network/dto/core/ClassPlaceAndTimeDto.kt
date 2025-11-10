package com.wafflestudio.snutt2.lib.network.dto.core

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// 신규
@JsonClass(generateAdapter = true)
data class ClassPlaceAndTimeDto(
    @param:Json(name = "day") val day: String,
    @param:Json(name = "place") val place: String?,
    @param:Json(name = "startMinute") val startMinute: Int,
    @param:Json(name = "endMinute") val endMinute: Int,
) {
    val startTimeInFloat: Float
        get() = startMinute / 60f

    val endTimeInFloat: Float
        get() = endMinute / 60f

    val startTimeHour: Int
        get() = startMinute / 60

    val startTimeMinute: Int
        get() = startMinute % 60

    val endTimeHour: Int
        get() = endMinute / 60

    val endTimeMinute: Int
        get() = endMinute % 60

    companion object {
        val Default = ClassPlaceAndTimeDto(
            day = "0",
            place = "",
            startMinute = 570,
            endMinute = 645,
        )
    }
}
