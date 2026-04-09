package com.wafflestudio.snutt2.domain.model

data class Friend(
    val id: String,
    val userId: String,
    val displayName: String?,
    val nickname: Nickname,
    val createdAt: String,
)

data class Nickname(
    val nickname: String,
    val tag: String,
) {
    fun getDisplayName(): String {
        return "$nickname#$tag"
    }
}

enum class FriendState {
    ACTIVE,
    REQUESTED,
    REQUESTING,
}
