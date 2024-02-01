package com.wafflestudio.snutt2.lib.network.dto.core

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.model.Campus
import com.wafflestudio.snutt2.model.GeoCoordinate
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class LectureBuildingDto(
    @Json(name = "id") val id: String? = null,
    @Json(name = "buildingNumber") val buildingNumber: String,
    @Json(name = "buildingNameKor") val buildingNameKor: String? = null,
    @Json(name = "buildingNameEng") val buildingNameEng: String? = null,
    @Json(name = "locationInDMS") val locationInDMS: GeoCoordinate,
    @Json(name = "locationInDecimal") val locationInDecimal: GeoCoordinate,
    @Json(name = "campus") val campus: Campus,
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        return (other as? LectureBuildingDto)?.let {
            (buildingNumber == other.buildingNumber) && (locationInDMS == other.locationInDMS)
        } ?: false
    }

    override fun hashCode(): Int {
        return buildingNumber.hashCode() + locationInDMS.hashCode()
    }
}
