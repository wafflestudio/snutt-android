package com.wafflestudio.snutt2.lib.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.core.network.model.PostCheckEmailByIdResults as PostCheckEmailByIdResultsNetwork

@JsonClass(generateAdapter = true)
data class PostCheckEmailByIdResults(
    @Json(name = "email") val email: String,
)

fun PostCheckEmailByIdResultsNetwork.toExternalModel() = PostCheckEmailByIdResults(
    email = this.email,
)
