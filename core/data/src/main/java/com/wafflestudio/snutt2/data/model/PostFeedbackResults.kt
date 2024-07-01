package com.wafflestudio.snutt2.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PostFeedbackResultsT(
    val message: String? = null,
)
