package com.wafflestudio.snutt2.data.themes

import com.wafflestudio.snutt2.lib.network.dto.core.ColorDto
import com.wafflestudio.snutt2.model.BuiltInTheme
import com.wafflestudio.snutt2.model.CustomTheme
import com.wafflestudio.snutt2.model.CustomTheme1
import kotlinx.coroutines.flow.StateFlow

interface ThemeRepository {

    val customThemes: StateFlow<List<CustomTheme>>

    val builtInThemes: StateFlow<List<BuiltInTheme>>

    suspend fun fetchThemes()

    fun getTheme(themeId: String): CustomTheme1

    suspend fun createTheme(name: String, colors: List<ColorDto>): CustomTheme1

    suspend fun updateTheme(themeId: String, name: String, colors: List<ColorDto>): CustomTheme1

    suspend fun copyTheme(themeId: String)

    suspend fun deleteTheme(themeId: String)
}
