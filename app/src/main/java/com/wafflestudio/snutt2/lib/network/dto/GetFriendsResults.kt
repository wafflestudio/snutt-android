package com.wafflestudio.snutt2.lib.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.lib.network.dto.core.FriendDto

// 신규
@JsonClass(generateAdapter = true)
data class GetFriendsResults(
    @param:Json(name = "content") val content: List<FriendDto>,
    @param:Json(name = "totalCount") val totalCount: Int,
)
