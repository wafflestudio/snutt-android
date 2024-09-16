package com.wafflestudio.snutt2.core.database.model

data class NetworkLog(
    val requestMethod: String,
    val requestUrl: String,
    val requestHeader: String,
    val requestBody: String,
    val responseCode: String,
    val responseBody: String,
)
