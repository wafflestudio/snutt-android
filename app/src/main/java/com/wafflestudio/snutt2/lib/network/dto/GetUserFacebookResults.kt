package com.wafflestudio.snutt2.lib.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.core.network.model.GetUserFacebookResults as GetUserFacebookResultsNetwork

@JsonClass(generateAdapter = true)
data class GetUserFacebookResults(
    @Json(name = "name") val name: String,
    @Json(name = "attached") val attached: Boolean,
)

fun GetUserFacebookResultsNetwork.toExternalModel() = GetUserFacebookResults(
    name = this.name,
    attached = this.attached,
)