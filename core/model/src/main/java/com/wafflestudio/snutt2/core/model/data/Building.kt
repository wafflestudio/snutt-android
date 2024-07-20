package com.wafflestudio.snutt2.core.model.data

data class Building(
    val campus: Campus,
    val buildingNumber: String,
    val buildingNameKor: String,
    val buildingNameEng: String,
    val coordinate: GeoCoordinate
)