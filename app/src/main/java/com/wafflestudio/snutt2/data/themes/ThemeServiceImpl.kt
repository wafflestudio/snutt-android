package com.wafflestudio.snutt2.data.themes

import com.wafflestudio.snutt2.domain.ThemeService
import com.wafflestudio.snutt2.domain.model.BuiltInTheme
import com.wafflestudio.snutt2.domain.model.Table
import com.wafflestudio.snutt2.domain.model.TableTheme
import com.wafflestudio.snutt2.domain.model.ThemeReference
import javax.inject.Inject

class ThemeServiceImpl @Inject constructor(
    private val themeRepository: ThemeRepository,
) : ThemeService {

    override suspend fun resolveTheme(table: Table): TableTheme = when (table.themeRef) {
        is ThemeReference.BuiltIn -> BuiltInTheme.fromCode(table.themeRef.code)
        is ThemeReference.Custom -> themeRepository.getTheme(table.themeRef.themeId)
    }
}
