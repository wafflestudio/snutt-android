package com.wafflestudio.snutt2.lib.network.dto.core

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.core.database.model.ClassTime
import com.wafflestudio.snutt2.core.network.model.ClassTimeDto as ClassTimeDtoNetwork

@JsonClass(generateAdapter = true)
data class ClassTimeDto(
    @Json(name = "day") val day: Int,
    @Json(name = "place") val place: String,
    @Json(name = "_id") val id: String? = null,
    @Json(name = "startMinute") val startMinute: Int = 0,
    @Json(name = "endMinute") val endMinute: Int = 0,
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

fun ClassTimeDtoNetwork.toExternalModel() = ClassTimeDto(
    day = day,
    place = place,
    id = id,
    startMinute = startMinute,
    endMinute = endMinute,
)

fun ClassTimeDto.toNetworkModel() = ClassTimeDtoNetwork(
    day = day,
    place = place,
    id = id,
    startMinute = startMinute,
    endMinute = endMinute,
)

fun ClassTime.toExternalModel() = ClassTimeDto(
    day = day,
    place = place,
    id = id,
    startMinute = startMinute,
    endMinute = endMinute,
)
