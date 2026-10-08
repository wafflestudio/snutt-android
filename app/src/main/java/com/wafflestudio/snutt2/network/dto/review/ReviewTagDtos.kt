package com.wafflestudio.snutt2.network.dto.review

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchTagGroupsDto(
    @param:Json(name = "tag_groups") val tagGroups: List<ReviewTagGroupDto>,
)

@JsonClass(generateAdapter = true)
data class ReviewTagGroupDto(
    val id: Int,
    val name: String,
    val ordering: Int,
    val color: String? = null,
    val tags: List<ReviewTagDto>,
)

@JsonClass(generateAdapter = true)
data class ReviewTagDto(
    val id: Long,
    val name: String,
    val description: String? = null,
    val ordering: Int,
)
