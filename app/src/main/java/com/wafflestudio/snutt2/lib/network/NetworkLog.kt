package com.wafflestudio.snutt2.lib.network

data class NetworkLog(
    val requestMethod: String,
    val requestUrl: String,
    val requestHeader: String,
    val requestBody: String,
    val responseCode: String,
    val responseBody: String,
)
