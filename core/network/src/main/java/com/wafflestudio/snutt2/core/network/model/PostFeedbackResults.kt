package com.wafflestudio.snutt2.core.network.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PostFeedbackResults(
    val message: String? = null,
)
