package com.wafflestudio.snutt2.lib.network.dto.core

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.model.Campus
import com.wafflestudio.snutt2.model.GeoCoordinate
import com.wafflestudio.snutt2.model.toExternalModel
import com.wafflestudio.snutt2.core.network.model.LectureBuildingDto as LectureBuildingDtoNetwork

@JsonClass(generateAdapter = true)
data class LectureBuildingDto(
    @Json(name = "id") val id: String? = null,
    @Json(name = "buildingNumber") val buildingNumber: String,
    @Json(name = "buildingNameKor") val buildingNameKor: String? = null,
    @Json(name = "buildingNameEng") val buildingNameEng: String? = null,
    @Json(name = "locationInDMS") val locationInDMS: GeoCoordinate,
    @Json(name = "locationInDecimal") val locationInDecimal: GeoCoordinate,
    @Json(name = "campus") val campus: Campus,
)

fun LectureBuildingDtoNetwork.toExternalModel() = LectureBuildingDto(
    id = this.id,
    buildingNumber = this.buildingNumber,
    buildingNameKor = this.buildingNameKor,
    buildingNameEng = this.buildingNameEng,
    locationInDMS = this.locationInDMS.toExternalModel(),
    locationInDecimal = this.locationInDecimal.toExternalModel(),
    campus = this.campus.toExternalModel(),
)
