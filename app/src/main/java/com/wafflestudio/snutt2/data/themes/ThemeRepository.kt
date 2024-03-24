package com.wafflestudio.snutt2.data.themes

import com.wafflestudio.snutt2.lib.network.dto.core.ColorDto
import com.wafflestudio.snutt2.model.BuiltInTheme
import com.wafflestudio.snutt2.model.CustomTheme
import com.wafflestudio.snutt2.model.TableTheme
import kotlinx.coroutines.flow.StateFlow

interface ThemeRepository {

    val customThemes: StateFlow<List<CustomTheme>>

    val builtInThemes: StateFlow<List<BuiltInTheme>>

    val currentTableTheme: StateFlow<TableTheme> // FIXME: 이게 ThemeRepository에 있는 게 맞나... ThemeRepository와 CurrentTableRepository에 의존하는 UseCase를 만들어야 할까?

    suspend fun fetchThemes()

    fun getTheme(themeId: String): CustomTheme

    suspend fun createTheme(name: String, colors: List<ColorDto>): CustomTheme

    suspend fun updateTheme(themeId: String, name: String, colors: List<ColorDto>): CustomTheme

    suspend fun copyTheme(themeId: String)

    suspend fun deleteTheme(themeId: String)
}
