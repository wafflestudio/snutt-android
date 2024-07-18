package com.wafflestudio.snutt2.core.network

import com.wafflestudio.snutt2.core.network.model.PostAccessTokenByAuthCodeParams
import com.wafflestudio.snutt2.core.network.model.PostAccessTokenByAuthCodeResults

interface SNUTTNetworkDataSourceForGoogle {
    suspend fun _getAccessTokenByAuthCode(
        body: PostAccessTokenByAuthCodeParams,
    ): PostAccessTokenByAuthCodeResults
}
