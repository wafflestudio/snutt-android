package com.wafflestudio.snutt2.lib.network.dto.core

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class ClassTimeDto(
    @Json(name = "day") val day: Int,
    @Json(name = "place") val place: String,
    @Json(name = "_id") val id: String? = null,
    @Json(name = "startMinute") val startMinute: Int = 0,
    @Json(name = "endMinute") val endMinute: Int = 0,
    @Json(name = "start_time") val start_time: String = "",
    @Json(name = "end_time") val end_time: String = "",
    @Json(name = "start") val start: Float? = null, // deprecated
    @Json(name = "len") val len: Float? = null, // deprecated
) : Parcelable {

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
