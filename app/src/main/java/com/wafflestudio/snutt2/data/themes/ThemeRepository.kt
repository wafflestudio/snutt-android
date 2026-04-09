package com.wafflestudio.snutt2.data.themes

import com.wafflestudio.snutt2.domain.model.BuiltInTheme
import com.wafflestudio.snutt2.domain.model.CustomTheme
import com.wafflestudio.snutt2.domain.model.ThemeColor
import com.wafflestudio.snutt2.data.Result
import kotlinx.coroutines.flow.StateFlow

interface ThemeRepository {

    val customThemes: StateFlow<List<CustomTheme>>

    val builtInThemes: StateFlow<List<BuiltInTheme>>

    fun getTheme(themeId: String): CustomTheme

    suspend fun fetchThemes(): Result<Unit>

    suspend fun createTheme(name: String, colors: List<ThemeColor>): Result<CustomTheme>

    suspend fun updateTheme(themeId: String, name: String, colors: List<ThemeColor>): Result<CustomTheme>

    suspend fun copyTheme(themeId: String): Result<Unit>

    suspend fun deleteTheme(themeId: String): Result<Unit>
}
