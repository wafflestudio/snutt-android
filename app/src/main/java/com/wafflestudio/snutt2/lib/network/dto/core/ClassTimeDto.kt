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
    @Json(name = "start_time") val start_time: String = "",
    @Json(name = "end_time") val end_time: String = "",
) : Parcelable {

    val startTimeInFloat: Float
        get() = start_time.split(':')[0].toFloat() + start_time.split(':')[1].toFloat() / 60f

    val endTimeInFloat: Float
        get() = end_time.split(':')[0].toFloat() + end_time.split(':')[1].toFloat() / 60f

    val startTimeHour: Float
        get() = start_time.split(':')[0].toFloat()

    val startTimeMinute: Int
        get() = start_time.split(':')[1].toInt()

    val endTimeHour: Float
        get() = end_time.split(':')[0].toFloat()

    val endTimeMinute: Int
        get() = end_time.split(':')[1].toInt()
}
