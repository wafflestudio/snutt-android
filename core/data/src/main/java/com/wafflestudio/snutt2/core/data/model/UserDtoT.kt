package com.wafflestudio.snutt2.core.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserDtoT(
    @Json(name = "isAdmin") val isAdmin: Boolean = false,
    @Json(name = "regDate") val regDate: String? = null,
    @Json(name = "notificationCheckedAt") val notificationCheckedAt: String? = null,
    @Json(name = "email") val email: String? = null,
    @Json(name = "localId") val localId: String? = null,
    @Json(name = "fbName") val fbName: String? = null,
    @Json(name = "nickname") val nickname: NicknameDtoT? = null,
)
