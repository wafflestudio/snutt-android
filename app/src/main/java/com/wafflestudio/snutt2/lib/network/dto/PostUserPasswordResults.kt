package com.wafflestudio.snutt2.lib.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.core.network.model.PostUserPasswordResults as PostUserPasswordResultsNetwork

@JsonClass(generateAdapter = true)
data class PostUserPasswordResults(
    @Json(name = "token") val token: String,
)

fun PostUserPasswordResultsNetwork.toExternalModel() = PostUserPasswordResults(
    token = this.token,
)