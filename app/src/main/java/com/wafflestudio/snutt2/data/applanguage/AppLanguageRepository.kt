package com.wafflestudio.snutt2.data.applanguage

import com.wafflestudio.snutt2.domain.model.AppLanguage
import kotlinx.coroutines.flow.StateFlow

interface AppLanguageRepository {
    val appLanguage: StateFlow<AppLanguage>
}
