package com.wafflestudio.snutt2.data.important_notice

import com.wafflestudio.snutt2.core.network.SNUTTNetworkDataSource
import com.wafflestudio.snutt2.model.ImportantNotice
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImportantNoticeRepositoryImpl @Inject constructor(
    private val api: SNUTTNetworkDataSource,
) : ImportantNoticeRepository {
    override suspend fun getConfigs(): ImportantNotice {
        val remoteConfig = api._getRemoteConfig().noticeConfig
        return if (remoteConfig?.visible == true) {
            ImportantNotice(title = remoteConfig.title ?: "", content = remoteConfig.content ?: "")
        } else {
            ImportantNotice(title = null, content = null)
        }
    }
}
