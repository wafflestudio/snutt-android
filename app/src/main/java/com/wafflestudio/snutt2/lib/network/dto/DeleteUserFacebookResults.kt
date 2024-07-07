package com.wafflestudio.snutt2.lib.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.core.network.model.DeleteUserFacebookResults as DeleteUserFacebookResultsNetwork

@JsonClass(generateAdapter = true)
data class DeleteUserFacebookResults(
    @Json(name = "token") val token: String,
)

fun DeleteUserFacebookResultsNetwork.toExternalModel() = DeleteUserFacebookResults(
    token = this.token,
)