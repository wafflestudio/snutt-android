package com.wafflestudio.snutt2.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GeoCoordinateT(
    @Json(name = "latitude") val latitude: Double,
    @Json(name = "longitude") val longitude: Double,
)
