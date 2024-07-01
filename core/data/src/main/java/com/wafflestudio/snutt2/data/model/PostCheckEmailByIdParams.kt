package com.wafflestudio.snutt2.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PostCheckEmailByIdParamsT(
    @Json(name = "user_id") val id: String,
)
