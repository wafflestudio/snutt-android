package com.wafflestudio.snutt2.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetNotificationCountResultsT(
    @Json(name = "count") val count: Long,
)
