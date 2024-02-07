package com.wafflestudio.snutt2.model

import android.os.Parcelable
import com.naver.maps.geometry.LatLng
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class GeoCoordinate(
    @Json(name = "latitude") val latitude: Double,
    @Json(name = "longitude") val longitude: Double,
) : Parcelable {
    fun toLatLng(): LatLng = LatLng(latitude, longitude)
}
