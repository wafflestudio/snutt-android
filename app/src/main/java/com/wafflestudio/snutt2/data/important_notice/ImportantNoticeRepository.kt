package com.wafflestudio.snutt2.data.important_notice

import com.wafflestudio.snutt2.model.ImportantNotice

interface ImportantNoticeRepository {
    suspend fun getConfigs(): ImportantNotice
}
