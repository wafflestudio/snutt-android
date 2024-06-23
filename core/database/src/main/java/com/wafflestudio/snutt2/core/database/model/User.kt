package com.wafflestudio.snutt2.core.database.model

import com.squareup.moshi.Json

data class User(
    @Json(name = "id") val id: String? = null,
    @Json(name = "isAdmin") val isAdmin: Boolean = false,
    @Json(name = "regDate") val regDate: String? = null,
    @Json(name = "notificationCheckedAt") val notificationCheckedAt: String? = null,
    @Json(name = "email") val email: String? = null,
    @Json(name = "localId") val localId: String? = null,
    @Json(name = "fbName") val fbName: String? = null,
    @Json(name = "nickname") val nickname: Nickname? = null,
)
