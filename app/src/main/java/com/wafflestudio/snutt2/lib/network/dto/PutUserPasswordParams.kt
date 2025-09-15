package com.wafflestudio.snutt2.lib.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PutUserPasswordParams(
    @param:Json(name = "new_password") val newPassword: String,
    @param:Json(name = "old_password") val oldPassword: String,
)
