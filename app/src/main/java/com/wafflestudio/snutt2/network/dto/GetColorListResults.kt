package com.wafflestudio.snutt2.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

data class Color(val fg: String, val bg: String)

@JsonClass(generateAdapter = true)
data class GetColorListResults(
    @Json(name = "message") val message: String,
    @Json(name = "colors") val colors: List<Color>,
    @Json(name = "names") val names: List<String>
)
