package com.wafflestudio.snutt2.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LectureBuildingDto(
    @param:Json(name = "id") val id: String? = null,
    @param:Json(name = "buildingNumber") val buildingNumber: String,
    @param:Json(name = "buildingNameKor") val buildingNameKor: String? = null,
    @param:Json(name = "buildingNameEng") val buildingNameEng: String? = null,
    @param:Json(name = "locationInDMS") val locationInDMS: GeoCoordinateDTO,
    @param:Json(name = "locationInDecimal") val locationInDecimal: GeoCoordinateDTO,
    @param:Json(name = "campus") val campus: CampusDto,
)
