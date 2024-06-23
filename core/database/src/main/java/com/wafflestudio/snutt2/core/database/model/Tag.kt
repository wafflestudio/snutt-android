package com.wafflestudio.snutt2.core.database.model

import com.squareup.moshi.JsonClass

// TODO: naming 변경 제안
@JsonClass(generateAdapter = true)
data class Tag(
    val type: TagType,
    val name: String,
)
