package com.wafflestudio.snutt2.core.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LectureBuildingDtoT(
    @Json(name = "id") val id: String? = null,
    @Json(name = "buildingNumber") val buildingNumber: String,
    @Json(name = "buildingNameKor") val buildingNameKor: String? = null,
    @Json(name = "buildingNameEng") val buildingNameEng: String? = null,
    @Json(name = "locationInDMS") val locationInDMS: GeoCoordinateT,
    @Json(name = "locationInDecimal") val locationInDecimal: GeoCoordinateT,
    @Json(name = "campus") val campus: CampusT,
)
