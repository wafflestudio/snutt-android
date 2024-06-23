package com.wafflestudio.snutt2.core.database.model

import com.squareup.moshi.Json

// TODO: 스펙 변경 반영
data class User(
    @Json(name = "isAdmin") val isAdmin: Boolean = false,
    @Json(name = "regDate") val regDate: String? = null,
    @Json(name = "notificationCheckedAt") val notificationCheckedAt: String? = null,
    @Json(name = "email") val email: String? = null,
    @Json(name = "localId") val localId: String? = null,
    @Json(name = "fbName") val fbName: String? = null,
    @Json(name = "nickname") val nickname: Nickname? = null,
)
