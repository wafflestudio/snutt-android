package com.wafflestudio.snutt2.model

import com.naver.maps.geometry.LatLng
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GeoCoordinate(
    @Json(name = "latitude") val latitude: Double,
    @Json(name = "longitude") val longitude: Double,
) {
    fun toLatLng(): LatLng = LatLng(latitude, longitude)
}
