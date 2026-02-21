package com.wafflestudio.snutt2.data.themes

import com.wafflestudio.snutt2.domainmodel.BuiltInTheme
import com.wafflestudio.snutt2.domainmodel.CustomTheme
import com.wafflestudio.snutt2.domainmodel.ThemeColor
import kotlinx.coroutines.flow.StateFlow

interface ThemeRepository {

    val customThemes: StateFlow<List<CustomTheme>>

    val builtInThemes: StateFlow<List<BuiltInTheme>>

    suspend fun fetchThemes()

    fun getTheme(themeId: String): CustomTheme

    suspend fun createTheme(name: String, colors: List<ThemeColor>): CustomTheme

    suspend fun updateTheme(themeId: String, name: String, colors: List<ThemeColor>): CustomTheme

    suspend fun copyTheme(themeId: String)

    suspend fun deleteTheme(themeId: String)
}
