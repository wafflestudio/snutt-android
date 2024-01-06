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
            isDefault = false,
            name = "커스텀테마 $it",
            colors = List(Random.nextInt(1, 9)) {
                ColorDto(fgColor = 0xffffff, bgColor = Random.nextInt(0, 0xffffff))
            },
        )
    }.apply { addAll(ThemeDto.builtInThemes.toMutableList().apply { set(0, first().copy(isDefault = true)) }) }

    override suspend fun getThemes(): List<ThemeDto> {
        return dummy.toList()
    }

    override suspend fun getTheme(themeId: Long): ThemeDto {
        return dummy.find { it.id == themeId } ?: ThemeDto.NewCustomTheme
    }

    override suspend fun createTheme(themeDto: ThemeDto): ThemeDto {
        val newTheme = themeDto.copy(id = Random.nextLong())
        dummy.add(newTheme)
        return newTheme
    }

    override suspend fun updateTheme(themeDto: ThemeDto): ThemeDto {
        dummy.indexOfFirst { it.id == themeDto.id }.let {
            dummy.set(it, themeDto)
        }
        return themeDto
    }

    override suspend fun deleteTheme(themeDto: ThemeDto) {
        dummy.removeIf { it.id == themeDto.id }
    }

    override suspend fun setDefaultTheme(themeId: Long) {
        dummy.forEachIndexed { idx, theme ->
            dummy[idx] = theme.copy(isDefault = (theme.isCustom && theme.id == themeId))
        }
    }

    override suspend fun setDefaultTheme(code: Int) {
        dummy.forEachIndexed { idx, theme ->
            dummy[idx] = theme.copy(isDefault = (theme.isCustom.not() && theme.code == code))
        }
    }
}
