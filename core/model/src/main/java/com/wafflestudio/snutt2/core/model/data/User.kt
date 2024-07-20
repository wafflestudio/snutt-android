package com.wafflestudio.snutt2.core.model.data

data class User(
    val isAdmin: Boolean,
    val email: String?,
    val localId: String?,
    val facebookName: String?,
    val nickname: Nickname?,
)