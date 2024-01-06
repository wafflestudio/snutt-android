package com.wafflestudio.snutt2.data.themes

import com.wafflestudio.snutt2.lib.network.dto.core.ThemeDto

interface ThemeRepository {
    suspend fun getThemes(): List<ThemeDto>

    suspend fun getTheme(themeId: Long): ThemeDto

    suspend fun createTheme(themeDto: ThemeDto): ThemeDto

    suspend fun updateTheme(themeDto: ThemeDto): ThemeDto

    suspend fun deleteTheme(themeDto: ThemeDto)

    suspend fun setDefaultTheme(themeId: Long)

    suspend fun setDefaultTheme(code: Int)
}
