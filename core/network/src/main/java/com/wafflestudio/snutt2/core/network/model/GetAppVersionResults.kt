package com.wafflestudio.snutt2.core.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetAppVersionResults(
    @Json(name = "version") val version: String,
)

// TODO : 얘는 안쓰고 있음
