package com.wafflestudio.snutt2.model

import android.os.Parcelable
import com.naver.maps.geometry.LatLng
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import kotlin.math.roundToInt

@JsonClass(generateAdapter = true)
@Parcelize
data class GeoCoordinate(
    @Json(name = "latitude") val latitude: Double,
    @Json(name = "longitude") val longitude: Double,
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        return (other as? GeoCoordinate)?.let {
            (latitude == other.latitude) && (longitude == other.longitude)
        } ?: false
    }

    override fun hashCode(): Int {
        return ((latitude - 37) * 1_000_000_000 + longitude * 100000).roundToInt()
    }

    fun toLatLng(): LatLng = LatLng(latitude, longitude)
}
