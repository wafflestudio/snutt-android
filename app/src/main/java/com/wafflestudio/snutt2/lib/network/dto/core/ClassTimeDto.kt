package com.wafflestudio.snutt2.lib.network.dto.core

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class ClassTimeDto(
    @Json(name = "day") val day: Int,
    @Json(name = "start") val start: Float,
    @Json(name = "len") val len: Float,
    @Json(name = "place") val place: String,
    @Json(name = "_id") val id: String? = null
): Parcelable
