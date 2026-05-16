package com.wafflestudio.snutt2.storage.model

import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.domain.model.SearchTag

@JsonClass(generateAdapter = true)
data class TagLocalEntity(
    val type: TagTypeLocalEntity,
    val name: String,
)

fun TagLocalEntity.toDomainModel(): SearchTag.Regular = SearchTag.Regular(type.toDomainModel(), name)

fun SearchTag.Regular.toLocalEntity(): TagLocalEntity = TagLocalEntity(type.toLocalEntity(), name)
