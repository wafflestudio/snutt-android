package com.wafflestudio.snutt2.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class PutUserPasswordParams(
    @Json(name = "new_password") val newPassword: String,
    @Json(name = "old_password") val oldPassword: String,
)
