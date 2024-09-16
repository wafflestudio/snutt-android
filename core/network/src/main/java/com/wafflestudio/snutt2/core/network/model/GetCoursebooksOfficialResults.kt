package com.wafflestudio.snutt2.core.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetCoursebooksOfficialResults(
    @Json(name = "url") val url: String,
)
