package com.wafflestudio.snutt2.lib.network.dto.core

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import kotlin.math.roundToInt

@JsonClass(generateAdapter = true)
@Parcelize
data class ClassTimeDto(
    @Json(name = "day") val day: Int,
    @Json(name = "place") val place: String,
    @Json(name = "_id") val id: String? = null,
    @Json(name = "startMinute") val startMinute: Int,
    @Json(name = "endMinute") val endMinute: Int,
    @Json(name = "start_time") val start_time: String = "",
    @Json(name = "end_time") val end_time: String = "",
    @Json(name = "start") val start: Float? = null, // deprecated
    @Json(name = "len") val len: Float? = null, // deprecated
) : Parcelable {

    val startTimeInFloat: Float
        get() =
            if (start_time.isNotEmpty()) start_time.split(':')[0].toFloat() + start_time.split(':')[1].toFloat() / 60f
            else (start ?: 0f) + 8 // 구 클라 대응

    val endTimeInFloat: Float
        get() =
            if (end_time.isNotEmpty()) end_time.split(':')[0].toFloat() + end_time.split(':')[1].toFloat() / 60f
            else (start ?: 0f) + (len ?: 1.25f) + 8 // 구 클라 대응

    val startTimeHour: Int
        get() = startTimeInFloat.toInt()

    val startTimeMinute: Int
        get() = (60 * (startTimeInFloat - startTimeHour)).roundToInt()

    val endTimeHour: Int
        get() = endTimeInFloat.toInt()

    val endTimeMinute: Int
        get() = (60 * (endTimeInFloat - endTimeHour)).roundToInt()

    companion object {
        val Default = ClassTimeDto(
            day = 0,
            place = "",
            id = null,
            startMinute = 570,
            endMinute = 645,
            start_time = "09:30",
            end_time = "10:45",
        )
    }
}
