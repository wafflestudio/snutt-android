package com.wafflestudio.snutt2.network.api.google

import retrofit2.http.Body
import retrofit2.http.POST

interface SNUTTRestApiForGoogle {
    @POST("/token")
    suspend fun _getAccessTokenByAuthCode(
        @Body body: PostAccessTokenByAuthCodeParams,
    ): PostAccessTokenByAuthCodeResults
}
