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
    @Json(name = "start") val start: Float,     // old
    @Json(name = "len") val len: Float,         // old
) : Parcelable {

    val startTimeInFloat: Float
        get() =
            if (start_time.isNotEmpty()) start_time.split(':')[0].toFloat() + start_time.split(':')[1].toFloat() / 60f
            else start

    val endTimeInFloat: Float
        get() =
            if (end_time.isNotEmpty()) end_time.split(':')[0].toFloat() + end_time.split(':')[1].toFloat() / 60f
            else start + len

    val startTimeHour: Int
        get() = startTimeInFloat.toInt()

    val startTimeMinute: Int
        get() = start_time.split(':')[1].toInt()

    val endTimeHour: Int
        get() = endTimeInFloat.toInt()

    val endTimeMinute: Int
        get() = end_time.split(':')[1].toInt()
}
