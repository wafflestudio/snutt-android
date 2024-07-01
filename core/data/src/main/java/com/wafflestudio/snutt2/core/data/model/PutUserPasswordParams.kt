package com.wafflestudio.snutt2.core.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PutUserPasswordParamsT(
    @Json(name = "new_password") val newPassword: String,
    @Json(name = "old_password") val oldPassword: String,
)
