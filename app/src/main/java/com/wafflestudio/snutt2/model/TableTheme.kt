package com.wafflestudio.snutt2.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import com.wafflestudio.snutt2.lib.Selectable
import com.wafflestudio.snutt2.lib.network.dto.core.ColorDto
import com.wafflestudio.snutt2.lib.network.dto.core.TableDto
import com.wafflestudio.snutt2.ui.isDarkMode

sealed class TableTheme(
    open val name: String,
    private val lightColors: List<ColorDto>,
    private val darkColors: List<ColorDto>,
) {
    fun getColors(isDarkMode: Boolean) = if (isDarkMode) {
        darkColors
    } else {
        lightColors
    }

    val isEditable: Boolean
        get() {
            return when (this) {
                is CustomTheme -> isFromMarket
                is BuiltInTheme -> true
            }
        }

    val isNew: Boolean
        get() {
            return when (this) {
                is CustomTheme -> id.isEmpty()
                is BuiltInTheme -> false
            }
        }
}

class CustomTheme(
    val id: String,
    override val name: String,
    val isFromMarket: Boolean,
    colors: List<ColorDto>,
) : TableTheme(
    name = name,
    lightColors = colors,
    darkColors = colors,
) {
    fun isAppliedToTable(table: TableDto): Boolean = table.themeId == this.id

    companion object {
        val Default = CustomTheme(
            id = "",
            name = "새 커스텀 테마",
            isFromMarket = false,
            colors = listOf(ColorDto(fgColor = 0xffffff, bgColor = 0x1bd0c8)),
        )
    }
}

class BuiltInTheme(
    val code: Int,
    override val name: String,
    lightColors: List<ColorDto>,
    darkColors: List<ColorDto>,
) : TableTheme(
    name = name,
    lightColors = lightColors,
    darkColors = darkColors,
) {
    fun getColorByIndex(colorIndex: Long): Int {
        return getColors(false)[colorIndex.toInt() - 1].bgColor ?: 0xffffff
    }

    @Composable
    fun getColorByIndexComposable(colorIndex: Long): androidx.compose.ui.graphics.Color {
        return androidx.compose.ui.graphics.Color(getColors(isDarkMode())[colorIndex.toInt() - 1].bgColor ?: 0xffffff)
    }

    companion object {  // FIXME: SNUTT 외 테마들에 색깔 옮겨오기
        val SNUTT = BuiltInTheme(
            code = 0,
            name = "SNUTT",
            lightColors = listOf(
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#E54459"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#F58D3D"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#FAC42D"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#A6D930"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#2BC267"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#1BD0C8"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#1D99E8"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#4F48C4"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#AF56B3"),
            ),
            darkColors = listOf(
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#D95F71"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#DF6E3C"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#E68937"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#95B03E"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#419343"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#5BA0D7"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#58C1B7"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#3E35A7"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#783891"),
            ),
        )
        val MODERN = BuiltInTheme(
            code = 1,
            name = "모던",
            lightColors = listOf(
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#E54459"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#F58D3D"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#FAC42D"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#A6D930"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#2BC267"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#1BD0C8"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#1D99E8"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#4F48C4"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#AF56B3"),
            ),
            darkColors = listOf(
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#D95F71"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#DF6E3C"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#E68937"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#95B03E"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#419343"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#5BA0D7"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#58C1B7"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#3E35A7"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#783891"),
            ),
        )
        val AUTUMN = BuiltInTheme(
            code = 2,
            name = "가을",
            lightColors = listOf(
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#E54459"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#F58D3D"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#FAC42D"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#A6D930"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#2BC267"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#1BD0C8"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#1D99E8"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#4F48C4"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#AF56B3"),
            ),
            darkColors = listOf(
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#D95F71"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#DF6E3C"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#E68937"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#95B03E"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#419343"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#5BA0D7"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#58C1B7"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#3E35A7"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#783891"),
            ),
        )
        val CHERRY = BuiltInTheme(
            code = 3,
            name = "벚꽃",
            lightColors = listOf(
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#E54459"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#F58D3D"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#FAC42D"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#A6D930"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#2BC267"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#1BD0C8"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#1D99E8"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#4F48C4"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#AF56B3"),
            ),
            darkColors = listOf(
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#D95F71"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#DF6E3C"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#E68937"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#95B03E"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#419343"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#5BA0D7"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#58C1B7"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#3E35A7"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#783891"),
            ),
        )
        val ICE = BuiltInTheme(
            code = 4,
            name = "얼음",
            lightColors = listOf(
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#E54459"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#F58D3D"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#FAC42D"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#A6D930"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#2BC267"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#1BD0C8"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#1D99E8"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#4F48C4"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#AF56B3"),
            ),
            darkColors = listOf(
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#D95F71"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#DF6E3C"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#E68937"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#95B03E"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#419343"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#5BA0D7"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#58C1B7"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#3E35A7"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#783891"),
            ),
        )
        val GRASS = BuiltInTheme(
            code = 5,
            name = "잔디",
            lightColors = listOf(
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#E54459"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#F58D3D"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#FAC42D"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#A6D930"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#2BC267"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#1BD0C8"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#1D99E8"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#4F48C4"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#AF56B3"),
            ),
            darkColors = listOf(
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#D95F71"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#DF6E3C"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#E68937"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#95B03E"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#419343"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#5BA0D7"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#58C1B7"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#3E35A7"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#783891"),
            ),
        )

        fun fromCode(code: Int): BuiltInTheme {
            return when (code) {
                0 -> SNUTT
                1 -> MODERN
                2 -> AUTUMN
                3 -> CHERRY
                4 -> ICE
                5 -> GRASS
                else -> SNUTT
            }
        }
    }
}

data class EditingTheme(
    val name: String,
    val colors: List<Selectable<ColorDto>>,
    private val originalTheme: TableTheme,
    private val isDarkMode: Boolean,
) {
    val isEditable get() = originalTheme.isEditable
    val isNew get() = originalTheme.isNew
    val isCustomTheme get() = originalTheme is CustomTheme

    fun hasChange(): Boolean {
        return if (originalTheme.isEditable) {
            name != originalTheme.name ||
                colors.map { it.item } != originalTheme.getColors(isDarkMode)
        } else {
            false
        }
    }

    fun toTableTheme(): TableTheme {
        return when (originalTheme) {
            is CustomTheme -> {
                CustomTheme(
                    id = originalTheme.id,
                    name = name,
                    isFromMarket = originalTheme.isFromMarket,
                    colors = colors.map { it.item },
                )
            }

            is BuiltInTheme -> originalTheme
        }
    }

    companion object {
        fun fromTableTheme(tableTheme: TableTheme, isDarkMode: Boolean): EditingTheme {
            return EditingTheme(
                name = tableTheme.name,
                colors = tableTheme.getColors(isDarkMode).mapIndexed { index, colorDto ->
                    Selectable(colorDto, tableTheme.isEditable && index == 0)
                },
                originalTheme = tableTheme,
                isDarkMode = isDarkMode,
            )
        }
    }
}
