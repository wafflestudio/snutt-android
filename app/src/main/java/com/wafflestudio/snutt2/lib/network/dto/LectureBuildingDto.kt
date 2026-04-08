package com.wafflestudio.snutt2.lib.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.model.Campus
import com.wafflestudio.snutt2.model.GeoCoordinate

@JsonClass(generateAdapter = true)
data class LectureBuildingDto(
    @param:Json(name = "id") val id: String? = null,
    @param:Json(name = "buildingNumber") val buildingNumber: String,
    @param:Json(name = "buildingNameKor") val buildingNameKor: String? = null,
    @param:Json(name = "buildingNameEng") val buildingNameEng: String? = null,
    @param:Json(name = "locationInDMS") val locationInDMS: GeoCoordinate,
    @param:Json(name = "locationInDecimal") val locationInDecimal: GeoCoordinate,
    @param:Json(name = "campus") val campus: Campus,
)
