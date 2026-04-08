package com.wafflestudio.snutt2.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GeoCoordinateDTO(
    @param:Json(name = "latitude") val latitude: Double,
    @param:Json(name = "longitude") val longitude: Double,
)
