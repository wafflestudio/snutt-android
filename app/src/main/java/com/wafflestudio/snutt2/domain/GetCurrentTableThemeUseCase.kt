package com.wafflestudio.snutt2.domain

import com.wafflestudio.snutt2.data.tables.TableRepository
import com.wafflestudio.snutt2.data.themes.ThemeRepository
import com.wafflestudio.snutt2.domainmodel.BuiltInTheme
import com.wafflestudio.snutt2.domainmodel.TableTheme
import com.wafflestudio.snutt2.domainmodel.ThemeReference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetCurrentTableThemeUseCase @Inject constructor(
    private val themeRepository: ThemeRepository,
    private val tableRepository: TableRepository,
) {
    operator fun invoke(): Flow<TableTheme> {
        return combine(
            tableRepository.currentTable,
            themeRepository.customThemes,
        ) { table, _ ->
            table?.themeRef?.let { ref ->
                when (ref) {
                    is ThemeReference.Custom -> themeRepository.getTheme(ref.themeId)
                    is ThemeReference.BuiltIn -> BuiltInTheme.fromCode(ref.code)
                }
            } ?: BuiltInTheme.SNUTT
        }
    }
}
