package com.wafflestudio.snutt2.core.network.retrofit.google

import com.wafflestudio.snutt2.core.network.model.PostAccessTokenByAuthCodeParams
import com.wafflestudio.snutt2.core.network.model.PostAccessTokenByAuthCodeResults
import retrofit2.http.Body
import retrofit2.http.POST

interface SNUTTRestAPIForGoogleApi  {
    @POST("/token")
    suspend fun _getAccessTokenByAuthCode(
        @Body body: PostAccessTokenByAuthCodeParams,
    ): PostAccessTokenByAuthCodeResults
}