package com.wafflestudio.snutt2.fake

import com.wafflestudio.snutt2.RemoteConfig
import com.wafflestudio.snutt2.lib.network.dto.core.RemoteConfigDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeRemoteConfig : RemoteConfig {
    override val vacancyNotificationBannerEnabled = MutableStateFlow(false)
    override val sugangSNUUrl = MutableStateFlow("https://sugang.snu.ac.kr")
    override val settingPageNewBadgeTitles = MutableStateFlow<List<String>>(emptyList())
    override val disableMapFeature = MutableStateFlow(false)
    override val noticeConfig: Flow<RemoteConfigDto.NoticeConfig> =
        MutableStateFlow(RemoteConfigDto.NoticeConfig(false, null, null))
}
