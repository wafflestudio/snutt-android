package com.wafflestudio.snutt2.network.dto

import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.domain.model.TagType

@JsonClass(generateAdapter = true)
data class TagDto(
    val type: TagType,
    val name: String,
)
