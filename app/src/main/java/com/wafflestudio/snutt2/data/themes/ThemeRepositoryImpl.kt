package com.wafflestudio.snutt2.data.themes

import com.wafflestudio.snutt2.lib.network.dto.core.ColorDto
import com.wafflestudio.snutt2.lib.network.dto.core.ThemeDto
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class ThemeRepositoryImpl @Inject constructor() : ThemeRepository {

    val dummy = MutableList(15) {
        ThemeDto(
            id = Random.nextLong(),
            isCustom = true,
            isDefault = it == 14,
            name = "커스텀테마 $it",
            colors = List(Random.nextInt(1, 9)) {
                ColorDto(fgColor = 0xffffff, bgColor = Random.nextInt(0, 0xffffff))
            },
        )
    }

    override suspend fun getThemes(): List<ThemeDto> {
        return dummy.reversed()
    }

    override suspend fun getTheme(themeId: Long): ThemeDto {
        return dummy.find { it.id == themeId } ?: ThemeDto.NewCustomTheme
    }

    override suspend fun createTheme(themeDto: ThemeDto) {
        dummy.add(themeDto.copy(id = Random.nextLong()))
    }

    override suspend fun updateTheme(themeDto: ThemeDto) {
        dummy.indexOfFirst { it.id == themeDto.id }.let {
            dummy.set(it, themeDto)
        }
    }

    override suspend fun deleteTheme(themeDto: ThemeDto) {
        dummy.removeIf { it.id == themeDto.id }
    }
}
