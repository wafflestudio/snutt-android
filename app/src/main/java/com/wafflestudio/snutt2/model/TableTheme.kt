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
                is CustomTheme -> isFromMarket.not()
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

    companion object {
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
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#F0652A"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#F5AD3E"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#998F36"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#89C291"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#266F55"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#13808F"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#366689"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#432920"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#D82F3D"),
            ),
            darkColors = listOf(
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#BB592F"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#E08B45"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#B4B194"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#5B967C"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#266F55"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#13808F"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#426586"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#5C4335"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#AD2F31"),
            ),
        )
        val AUTUMN = BuiltInTheme(
            code = 2,
            name = "가을",
            lightColors = listOf(
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#B82E31"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#DB701C"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#EAA32A"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#C6C013"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#3A856E"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#19B2AC"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#3994CE"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#3F3A9C"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#924396"),
            ),
            darkColors = listOf(
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#A93A36"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#D56738"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#CC973F"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#A0942F"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#4E8370"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#29625A"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#4171A2"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#4F48C4"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#783891"),
            ),
        )
        val CHERRY = BuiltInTheme(
            code = 3,
            name = "벚꽃",
            lightColors = listOf(
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#FD79A8"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#FEC9DD"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#FEB0CC"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#FE93BF"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#E9B1D0"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#C67D97"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#BB8EA7"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#BDB4BF"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#E16597"),
            ),
            darkColors = listOf(
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#A43C58"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#7C164F"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#99446E"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#A77085"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#B290B8"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#BDB4BF"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#BB8EA7"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#736C75"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#C76F92"),
            ),
        )
        val ICE = BuiltInTheme(
            code = 4,
            name = "얼음",
            lightColors = listOf(
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#AABDCF"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#C0E9E8"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#66B6CA"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#015F95"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#A8D0DB"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#458ED0"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#62A9D1"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#20363D"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#6D8A96"),
            ),
            darkColors = listOf(
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#014D79"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#788DA4"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#AEC1C9"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#48595B"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#1C6C8E"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#64909C"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#88B1C6"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#44576B"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#757C80"),
            ),
        )
        val GRASS = BuiltInTheme(
            code = 5,
            name = "잔디",
            lightColors = listOf(
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#4FBEAA"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#9FC1A4"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#5A8173"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#84AEB1"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#266F55"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#D0E0C4"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#59886D"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#476060"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#3D7068"),
            ),
            darkColors = listOf(
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#2D5A45"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#429587"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#86A99A"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#597B6A"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#42635B"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#586C5D"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#324845"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#AAB6B1"),
                ColorDto(fgRaw = "#FFFFFF", bgRaw = "#747877"),
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
