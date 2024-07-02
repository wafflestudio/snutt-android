package com.wafflestudio.snutt2.lib.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.core.network.model.PostUserFacebookResults as PostUserFacebookResultsNetwork

@JsonClass(generateAdapter = true)
data class PostUserFacebookResults(
    @Json(name = "token") val token: String,
)

fun PostUserFacebookResultsNetwork.toExternalModel() = PostUserFacebookResults(
    token = this.token,
)
