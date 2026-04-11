package com.wafflestudio.snutt2.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// 신규
@JsonClass(generateAdapter = true)
data class PostRequestFriendParams(
    @param:Json(name = "nickname") val nickname: String,
)
