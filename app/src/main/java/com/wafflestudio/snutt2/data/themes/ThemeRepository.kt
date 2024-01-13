package com.wafflestudio.snutt2.data.themes

import com.wafflestudio.snutt2.lib.network.dto.core.ColorDto
import com.wafflestudio.snutt2.model.BuiltInTheme
import com.wafflestudio.snutt2.model.CustomTheme
import kotlinx.coroutines.flow.StateFlow

interface ThemeRepository {

    val customThemes: StateFlow<List<CustomTheme>>

    val builtInThemes: StateFlow<List<BuiltInTheme>>

    suspend fun fetchThemes()

    fun getTheme(themeId: String): CustomTheme

    fun getTheme(code: Int): BuiltInTheme

    suspend fun createTheme(name: String, colors: List<ColorDto>)

    suspend fun updateTheme(themeId: String, name: String, colors: List<ColorDto>)

    suspend fun copyTheme(themeId: String)

    suspend fun deleteTheme(themeId: String)

    suspend fun setCustomThemeDefault(themeId: String)

    suspend fun setBuiltInThemeDefault(theme: Int)

    suspend fun unsetCustomThemeDefault(themeId: String)
}
