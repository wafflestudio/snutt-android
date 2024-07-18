package com.wafflestudio.snutt2.core.network.retrofit.google

import com.wafflestudio.snutt2.core.network.SNUTTNetworkDataSourceForGoogle
import com.wafflestudio.snutt2.core.network.model.PostAccessTokenByAuthCodeParams
import com.wafflestudio.snutt2.core.network.model.PostAccessTokenByAuthCodeResults
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SNUTTRestApiForGoogle @Inject constructor(
    private val networkApi: SNUTTRestAPIForGoogleApi,
) : SNUTTNetworkDataSourceForGoogle {
    override suspend fun _getAccessTokenByAuthCode(body: PostAccessTokenByAuthCodeParams): PostAccessTokenByAuthCodeResults =
        networkApi._getAccessTokenByAuthCode(body)
}
