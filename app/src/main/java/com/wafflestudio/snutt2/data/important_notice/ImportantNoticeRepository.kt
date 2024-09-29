package com.wafflestudio.snutt2.data.important_notice

import com.wafflestudio.snutt2.model.ImportantNotice
import kotlinx.coroutines.flow.StateFlow

interface ImportantNoticeRepository {
    suspend fun getConfigs(): ImportantNotice
}