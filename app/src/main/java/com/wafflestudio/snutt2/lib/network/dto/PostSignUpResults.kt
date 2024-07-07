package com.wafflestudio.snutt2.lib.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.core.network.model.PostSignUpResults as PostSignUpResultsNetwork

@JsonClass(generateAdapter = true)
data class PostSignUpResults(
    @Json(name = "message") val message: String,
    @Json(name = "token") val token: String,
    @Json(name = "user_id") val userId: String,
)

fun PostSignUpResultsNetwork.toExternalModel() = PostSignUpResults(
    message = this.message,
    token = this.token,
    userId = this.userId,
)
