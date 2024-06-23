package com.wafflestudio.snutt2.lib.network

import com.wafflestudio.snutt2.lib.network.dto.PostAccessTokenByAuthCodeParams
import com.wafflestudio.snutt2.lib.network.dto.PostAccessTokenByAuthCodeResults
import com.wafflestudio.snutt2.lib.network.dto.PostSearchQueryParams
import retrofit2.http.Body
import retrofit2.http.POST

interface SNUTTRestApiForGoogle {
    @POST("/token")
    suspend fun _getAccessTokenByAuthCode(
        @Body body: PostAccessTokenByAuthCodeParams,
    ): PostAccessTokenByAuthCodeResults
}
