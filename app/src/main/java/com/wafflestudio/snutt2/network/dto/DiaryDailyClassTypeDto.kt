package com.wafflestudio.snutt2.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DiaryDailyClassTypeDto(
    @param:Json(name = "id") val id: String,
    @param:Json(name = "name") val name: String,
)
