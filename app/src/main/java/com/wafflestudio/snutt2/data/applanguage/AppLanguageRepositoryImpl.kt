package com.wafflestudio.snutt2.data.applanguage

import com.wafflestudio.snutt2.domain.model.AppLanguage
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppLanguageRepositoryImpl @Inject constructor(
    appLanguageDataSource: AppLanguageDataSource,
) : AppLanguageRepository {
    override val appLanguage: StateFlow<AppLanguage> = appLanguageDataSource.appLanguage
}
