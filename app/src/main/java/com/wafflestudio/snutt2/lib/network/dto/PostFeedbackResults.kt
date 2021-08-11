package com.wafflestudio.snutt2.lib.network.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PostFeedbackResults(
    val message: String? = null
)
