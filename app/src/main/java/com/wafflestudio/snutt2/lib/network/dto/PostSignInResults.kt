package com.wafflestudio.snutt2.lib.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.core.network.model.PostSignInResults as PostSignInResultsNetwork

@JsonClass(generateAdapter = true)
data class PostSignInResults(
    @Json(name = "token") val token: String,
    @Json(name = "user_id") val userId: String,
)

fun PostSignInResultsNetwork.toExternalModel() = PostSignInResults(
    token = this.token,
    userId = this.userId,
)