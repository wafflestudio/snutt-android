package com.wafflestudio.snutt2.lib.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.core.network.model.PostLoginFacebookResults as PostLoginFacebookResultsNetwork

@JsonClass(generateAdapter = true)
data class PostSocialLoginResults(
    @Json(name = "user_id") val userId: String,
    @Json(name = "token") val token: String,
    @Json(name = "message") val message: String,
)

fun PostLoginFacebookResultsNetwork.toExternalModel() = PostLoginFacebookResults(
    token = this.token,
    userId = this.userId,
)