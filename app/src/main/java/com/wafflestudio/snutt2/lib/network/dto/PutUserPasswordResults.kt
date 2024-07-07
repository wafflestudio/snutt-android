package com.wafflestudio.snutt2.lib.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.core.network.model.PutUserPasswordResults as PutUserPasswordResultsNetwork

@JsonClass(generateAdapter = true)
data class PutUserPasswordResults(
    @Json(name = "token") val token: String,
)

fun PutUserPasswordResultsNetwork.toExternalModel() = PutUserPasswordResults(
    token = this.token,
)