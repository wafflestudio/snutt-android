package com.wafflestudio.snutt2.core.model.data

data class PlaceTime(
    val timetableBlock: TimetableBlock,
    val place: Place,
)

data class Place(
    val name: String,
    val building: Building
)