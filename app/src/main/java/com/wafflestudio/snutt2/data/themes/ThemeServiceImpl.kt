package com.wafflestudio.snutt2.data.themes

import com.wafflestudio.snutt2.domain.ThemeService
import com.wafflestudio.snutt2.domainmodel.BuiltInTheme
import com.wafflestudio.snutt2.domainmodel.Table
import com.wafflestudio.snutt2.domainmodel.TableTheme
import com.wafflestudio.snutt2.domainmodel.ThemeReference
import javax.inject.Inject

class ThemeServiceImpl @Inject constructor(
    private val themeRepository: ThemeRepository,
) : ThemeService {

    override suspend fun resolveTheme(table: Table): TableTheme {
        return when (table.themeRef) {
            is ThemeReference.BuiltIn -> BuiltInTheme.fromCode(table.themeRef.code)
            is ThemeReference.Custom -> themeRepository.getTheme(table.themeRef.themeId)
        }
    }
}