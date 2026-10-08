package com.wafflestudio.snutt2.domain.model.review

data class ReviewTagGroup(
    val id: Int,
    val name: String,
    val tags: List<ReviewTag>,
)

data class ReviewTag(
    val id: Long,
    val name: String,
)
