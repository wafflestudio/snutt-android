package com.wafflestudio.snutt2.data.themes

import com.wafflestudio.snutt2.lib.network.dto.core.ThemeDto

interface ThemeRepository {
    suspend fun getThemes(): List<ThemeDto>

    suspend fun getTheme(themeId: Long): ThemeDto

    suspend fun createTheme(themeDto: ThemeDto)

    suspend fun updateTheme(themeDto: ThemeDto)

    suspend fun deleteTheme(themeDto: ThemeDto)
}
