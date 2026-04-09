package com.wafflestudio.snutt2.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// 신규
@JsonClass(generateAdapter = true)
data class GetFriendRequestLinkResults(
    @param:Json(name = "requestToken") val requestToken: String,
)
