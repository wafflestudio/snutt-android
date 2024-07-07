package com.wafflestudio.snutt2.core.model.data

data class Nickname(
    val nickname: String,
    val tag: String,
) {
    override fun toString(): String {
        return "$nickname#$tag"
    }
}