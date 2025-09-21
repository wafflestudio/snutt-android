package com.wafflestudio.snutt2.domain

import com.wafflestudio.snutt2.data.current_table.CurrentTableRepository
import com.wafflestudio.snutt2.data.themes.ThemeRepository
import com.wafflestudio.snutt2.domainmodel.BuiltInTheme
import com.wafflestudio.snutt2.domainmodel.TableTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetCurrentTableThemeUseCase @Inject constructor(
    private val themeRepository: ThemeRepository,
    private val currentTableRepository: CurrentTableRepository,
) {
    operator fun invoke(): Flow<TableTheme> {
        return combine(
            currentTableRepository.currentTable,
            themeRepository.customThemes,
        ) { table, _ ->
            table?.themeId?.let { themeId ->
                themeRepository.getTheme(themeId)
            } ?: table?.theme?.let {
                BuiltInTheme.fromCode(it)
            } ?: BuiltInTheme.SNUTT
        }
    }
}
