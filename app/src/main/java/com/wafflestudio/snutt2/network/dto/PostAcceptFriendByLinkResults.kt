package com.wafflestudio.snutt2.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.network.dto.NicknameDto

// 신규
@JsonClass(generateAdapter = true)
data class PostAcceptFriendByLinkResults(
    @param:Json(name = "id") val id: String,
    @param:Json(name = "userId") val userId: String,
    @param:Json(name = "nickname") val nickname: NicknameDto,
)
