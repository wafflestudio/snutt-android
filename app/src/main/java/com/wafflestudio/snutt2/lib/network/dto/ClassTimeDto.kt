package com.wafflestudio.snutt2.lib.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ClassTimeDto(
    @param:Json(name = "day") val day: Int,
    @param:Json(name = "place") val place: String,
    @param:Json(name = "_id") val id: String? = null,
    @param:Json(name = "startMinute") val startMinute: Int = 0,
    @param:Json(name = "endMinute") val endMinute: Int = 0,
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
        val Default = ClassTimeDto(
            day = 0,
            place = "",
            id = null,
            startMinute = 570,
            endMinute = 645,
        )
    }
}
