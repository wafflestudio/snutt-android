package com.wafflestudio.snutt2.domainmodel

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.wafflestudio.snutt2.lib.Selectable
import com.wafflestudio.snutt2.lib.network.dto.core.TableDto
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.isDarkMode

sealed class TableTheme(
    open val name: String,
    private val lightColors: List<LectureColor>,
    private val darkColors: List<LectureColor>,
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
                is BuiltInTheme -> false
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
    colors: List<LectureColor>,
) : TableTheme(
    name = name,
    lightColors = colors,
    darkColors = colors,
) {
    fun isAppliedToTable(table: TableDto): Boolean = table.themeId == this.id

    companion object {
        val Default = CustomTheme(
            id = "",
            name = "새 테마",
            isFromMarket = false,
            colors = listOf(
                CustomColor(
                    foreground = SNUTTColors.White,
                    background = SNUTTColors.MainBlue,
                ),
            ),
        )
    }
}

class BuiltInTheme(
    val code: Int,
    override val name: String,
    lightColors: List<BuiltInColor>,
    darkColors: List<BuiltInColor>,
) : TableTheme(
    name = name,
    lightColors = lightColors,
    darkColors = darkColors,
) {
    fun getColorByIndex(colorIndex: Long): Color {
        return getColors(false)[colorIndex.toInt() - 1].background
    }

    @Composable
    fun getColorByIndexComposable(colorIndex: Long): Color {
        return getColors(isDarkMode())[colorIndex.toInt() - 1].background
    }

    companion object {
        val SNUTT = BuiltInTheme(
            code = 0,
            name = "SNUTT",
            lightColors = listOf(
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFFE54459), 0),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFFF58D3D), 1),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFFFAC42D), 2),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFFA6D930), 3),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFF2BC267), 4),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFF1BD0C8), 5),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFF1D99E8), 6),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFF4F48C4), 7),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFFAF56B3), 8),
            ),
            darkColors = listOf(
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFFD95F71), 0),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFFDF6E3C), 1),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFFE68937), 2),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFF95B03E), 3),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFF419343), 4),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFF5BA0D7), 5),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFF58C1B7), 6),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFF3E35A7), 7),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFF783891), 8),
            ),
        )
        val MODERN = BuiltInTheme(
            code = 1,
            name = "모던",
            lightColors = listOf(
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFFF0652A), 0),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFFF5AD3E), 1),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFF998F36), 2),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFF89C291), 3),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFF266F55), 4),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFF13808F), 5),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFF366689), 6),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFF432920), 7),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFFD82F3D), 8),
            ),
            darkColors = listOf(
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFFBB592F), 0),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFFE08B45), 1),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFFB4B194), 2),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFF5B967C), 3),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFF266F55), 4),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFF13808F), 5),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFF426586), 6),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFF5C4335), 7),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFFAD2F31), 8),
            ),
        )
        val AUTUMN = BuiltInTheme(
            code = 2,
            name = "가을",
            lightColors = listOf(
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFFB82E31), 0),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFFDB701C), 1),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFFEAA32A), 2),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFFC6C013), 3),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFF3A856E), 4),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFF19B2AC), 5),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFF3994CE), 6),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFF3F3A9C), 7),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFF924396), 8),
            ),
            darkColors = listOf(
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFFA93A36), 0),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFFD56738), 1),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFFCC973F), 2),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFFA0942F), 3),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFF4E8370), 4),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFF29625A), 5),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFF4171A2), 6),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFF4F48C4), 7),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFF783891), 8),
            ),
        )
        val CHERRY = BuiltInTheme(
            code = 3,
            name = "벚꽃",
            lightColors = listOf(
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFFFD79A8), 0),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFFFEC9DD), 1),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFFFEB0CC), 2),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFFFE93BF), 3),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFFE9B1D0), 4),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFFC67D97), 5),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFFBB8EA7), 6),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFFBDB4BF), 7),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFFE16597), 8),
            ),
            darkColors = listOf(
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFFA43C58), 0),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFF7C164F), 1),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFF99446E), 2),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFFA77085), 3),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFFB290B8), 4),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFFBDB4BF), 5),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFFBB8EA7), 6),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFF736C75), 7),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFFC76F92), 8),
            ),
        )
        val ICE = BuiltInTheme(
            code = 4,
            name = "얼음",
            lightColors = listOf(
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFFAABDCF), 0),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFFC0E9E8), 1),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFF66B6CA), 2),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFF015F95), 3),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFFA8D0DB), 4),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFF458ED0), 5),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFF62A9D1), 6),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFF20363D), 7),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFF6D8A96), 8),
            ),
            darkColors = listOf(
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFF014D79), 0),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFF788DA4), 1),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFFAEC1C9), 2),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFF48595B), 3),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFF1C6C8E), 4),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFF64909C), 5),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFF88B1C6), 6),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFF44576B), 7),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFF757C80), 8),
            ),
        )
        val GRASS = BuiltInTheme(
            code = 5,
            name = "잔디",
            lightColors = listOf(
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFF4FBEAA), 0),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFF9FC1A4), 1),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFF5A8173), 2),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFF84AEB1), 3),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFF266F55), 4),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFFD0E0C4), 5),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFF59886D), 6),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFF476060), 7),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFF3D7068), 8),
            ),
            darkColors = listOf(
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFF2D5A45), 0),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFF429587), 1),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFF86A99A), 2),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFF597B6A), 3),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFF42635B), 4),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFF586C5D), 5),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFF324845), 6),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFFAAB6B1), 7),
                BuiltInColor(foreground = Color(0xFFFFFFFF), background = Color(0xFF747877), 8),
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
    val colors: List<Selectable<LectureColor>>,
    private val originalTheme: TableTheme,
    private val isDarkMode: Boolean,
) {
    val isEditable get() = originalTheme.isEditable
    val isNew get() = originalTheme.isNew
    val isCustomTheme get() = originalTheme is CustomTheme
    val isFromMarket get() = originalTheme is CustomTheme && originalTheme.isFromMarket

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
