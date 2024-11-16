package com.wafflestudio.snutt2.model

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.lib.Selectable
import com.wafflestudio.snutt2.lib.network.dto.core.ColorDto
import com.wafflestudio.snutt2.lib.network.dto.core.TableDto
import com.wafflestudio.snutt2.ui.isDarkMode

sealed class TableTheme1(
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
                is CustomTheme1 -> isFromMarket
                is BuiltInTheme1 -> true
            }
        }

    val isNew: Boolean
        get() {
            return when (this) {
                is CustomTheme1 -> id.isEmpty()
                is BuiltInTheme1 -> false
            }
        }
}

class CustomTheme1(
    val id: String,
    override val name: String,
    val isFromMarket: Boolean,
    colors: List<ColorDto>,
) : TableTheme1(
    name = name,
    lightColors = colors,
    darkColors = colors,
) {
    fun isAppliedToTable(table: TableDto): Boolean = table.themeId == this.id

    companion object {
        val Default = CustomTheme1(
            id = "",
            name = "새 커스텀 테마",
            isFromMarket = false,
            colors = listOf(ColorDto(fgColor = 0xffffff, bgColor = 0x1bd0c8)),
        )
    }
}

class BuiltInTheme1(
    val code: Int,
    override val name: String,
    lightColors: List<ColorDto>,
    darkColors: List<ColorDto>,
) : TableTheme1(
    name = name,
    lightColors = lightColors,
    darkColors = darkColors,
) {
    companion object {  // FIXME: SNUTT 외 테마들에 색깔 옮겨오기
        val SNUTT = BuiltInTheme1(
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
        val MODERN = BuiltInTheme1(
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
        val AUTUMN = BuiltInTheme1(
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
        val CHERRY = BuiltInTheme1(
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
        val ICE = BuiltInTheme1(
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
        val GRASS = BuiltInTheme1(
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

        fun fromCode(code: Int): BuiltInTheme1 {
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
    private val originalTheme: TableTheme1,
    private val isDarkMode: Boolean,
) {
    val isEditable get() = originalTheme.isEditable
    val isNew get() = originalTheme.isNew
    val isCustomTheme get() = originalTheme is CustomTheme1

    fun hasChange(): Boolean {
        return if (originalTheme.isEditable) {
            name != originalTheme.name ||
                colors.map { it.item } != originalTheme.getColors(isDarkMode)
        } else {
            false
        }
    }

    fun toTableTheme(): TableTheme1 {
        return when (originalTheme) {
            is CustomTheme1 -> {
                CustomTheme1(
                    id = originalTheme.id,
                    name = name,
                    isFromMarket = originalTheme.isFromMarket,
                    colors = colors.map { it.item },
                )
            }

            is BuiltInTheme1 -> originalTheme
        }
    }

    companion object {
        fun fromTableTheme(tableTheme: TableTheme1, isDarkMode: Boolean): EditingTheme {
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


// ====== old ======

abstract class TableTheme(
    open val name: String,
)

data class CustomTheme(
    val id: String,
    override val name: String,
    val colors: List<ColorDto>,
) : TableTheme(name) {

    companion object {
        val Default = CustomTheme(
            id = "",
            name = "새 커스텀 테마",
            colors = listOf(ColorDto(fgColor = 0xffffff, bgColor = 0x1bd0c8)),
        )
    }
}

data class BuiltInTheme(
    val code: Int,
    override val name: String,
) : TableTheme(name) {

    companion object {
        val SNUTT = BuiltInTheme(
            code = 0,
            name = "SNUTT",
        )
        val MODERN = BuiltInTheme(
            code = 1,
            name = "모던",
        )
        val AUTUMN = BuiltInTheme(
            code = 2,
            name = "가을",
        )
        val CHERRY = BuiltInTheme(
            code = 3,
            name = "벚꽃",
        )
        val ICE = BuiltInTheme(
            code = 4,
            name = "얼음",
        )
        val GRASS = BuiltInTheme(
            code = 5,
            name = "잔디",
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

    fun getColorByIndex(context: Context, colorIndex: Long): Int {
        return when (code) {
            SNUTT.code -> listOf(
                context.getColor(R.color.theme_snutt_0),
                context.getColor(R.color.theme_snutt_1),
                context.getColor(R.color.theme_snutt_2),
                context.getColor(R.color.theme_snutt_3),
                context.getColor(R.color.theme_snutt_4),
                context.getColor(R.color.theme_snutt_5),
                context.getColor(R.color.theme_snutt_6),
                context.getColor(R.color.theme_snutt_7),
                context.getColor(R.color.theme_snutt_8),
            )

            MODERN.code -> listOf(
                context.getColor(R.color.theme_modern_0),
                context.getColor(R.color.theme_modern_1),
                context.getColor(R.color.theme_modern_2),
                context.getColor(R.color.theme_modern_3),
                context.getColor(R.color.theme_modern_4),
                context.getColor(R.color.theme_modern_5),
                context.getColor(R.color.theme_modern_6),
                context.getColor(R.color.theme_modern_7),
                context.getColor(R.color.theme_modern_8),
            )

            AUTUMN.code -> listOf(
                context.getColor(R.color.theme_autumn_0),
                context.getColor(R.color.theme_autumn_1),
                context.getColor(R.color.theme_autumn_2),
                context.getColor(R.color.theme_autumn_3),
                context.getColor(R.color.theme_autumn_4),
                context.getColor(R.color.theme_autumn_5),
                context.getColor(R.color.theme_autumn_6),
                context.getColor(R.color.theme_autumn_7),
                context.getColor(R.color.theme_autumn_8),
            )

            CHERRY.code -> listOf(
                context.getColor(R.color.theme_cherry_0),
                context.getColor(R.color.theme_cherry_1),
                context.getColor(R.color.theme_cherry_2),
                context.getColor(R.color.theme_cherry_3),
                context.getColor(R.color.theme_cherry_4),
                context.getColor(R.color.theme_cherry_5),
                context.getColor(R.color.theme_cherry_6),
                context.getColor(R.color.theme_cherry_7),
                context.getColor(R.color.theme_cherry_8),
            )

            ICE.code -> listOf(
                context.getColor(R.color.theme_ice_0),
                context.getColor(R.color.theme_ice_1),
                context.getColor(R.color.theme_ice_2),
                context.getColor(R.color.theme_ice_3),
                context.getColor(R.color.theme_ice_4),
                context.getColor(R.color.theme_ice_5),
                context.getColor(R.color.theme_ice_6),
                context.getColor(R.color.theme_ice_7),
                context.getColor(R.color.theme_ice_8),
            )

            GRASS.code -> listOf(
                context.getColor(R.color.theme_grass_0),
                context.getColor(R.color.theme_grass_1),
                context.getColor(R.color.theme_grass_2),
                context.getColor(R.color.theme_grass_3),
                context.getColor(R.color.theme_grass_4),
                context.getColor(R.color.theme_grass_5),
                context.getColor(R.color.theme_grass_6),
                context.getColor(R.color.theme_grass_7),
                context.getColor(R.color.theme_grass_8),
            )

            else -> {
                listOf(
                    context.getColor(R.color.theme_snutt_0),
                    context.getColor(R.color.theme_snutt_1),
                    context.getColor(R.color.theme_snutt_2),
                    context.getColor(R.color.theme_snutt_3),
                    context.getColor(R.color.theme_snutt_4),
                    context.getColor(R.color.theme_snutt_5),
                    context.getColor(R.color.theme_snutt_6),
                    context.getColor(R.color.theme_snutt_7),
                    context.getColor(R.color.theme_snutt_8),
                )
            }
        }[colorIndex.toInt() - 1]
    }

    @Composable
    fun getColorByIndexComposable(colorIndex: Long): androidx.compose.ui.graphics.Color {
        return if (isDarkMode()) {
            when (code) {
                SNUTT.code -> listOf(
                    colorResource(R.color.theme_snutt_dark_0),
                    colorResource(R.color.theme_snutt_dark_1),
                    colorResource(R.color.theme_snutt_dark_2),
                    colorResource(R.color.theme_snutt_dark_3),
                    colorResource(R.color.theme_snutt_dark_4),
                    colorResource(R.color.theme_snutt_dark_5),
                    colorResource(R.color.theme_snutt_dark_6),
                    colorResource(R.color.theme_snutt_dark_7),
                    colorResource(R.color.theme_snutt_dark_8),
                )

                MODERN.code -> listOf(
                    colorResource(R.color.theme_modern_dark_0),
                    colorResource(R.color.theme_modern_dark_1),
                    colorResource(R.color.theme_modern_dark_2),
                    colorResource(R.color.theme_modern_dark_3),
                    colorResource(R.color.theme_modern_dark_4),
                    colorResource(R.color.theme_modern_dark_5),
                    colorResource(R.color.theme_modern_dark_6),
                    colorResource(R.color.theme_modern_dark_7),
                    colorResource(R.color.theme_modern_dark_8),
                )

                AUTUMN.code -> listOf(
                    colorResource(R.color.theme_autumn_dark_0),
                    colorResource(R.color.theme_autumn_dark_1),
                    colorResource(R.color.theme_autumn_dark_2),
                    colorResource(R.color.theme_autumn_dark_3),
                    colorResource(R.color.theme_autumn_dark_4),
                    colorResource(R.color.theme_autumn_dark_5),
                    colorResource(R.color.theme_autumn_dark_6),
                    colorResource(R.color.theme_autumn_dark_7),
                    colorResource(R.color.theme_autumn_dark_8),
                )

                CHERRY.code -> listOf(
                    colorResource(R.color.theme_cherry_dark_0),
                    colorResource(R.color.theme_cherry_dark_1),
                    colorResource(R.color.theme_cherry_dark_2),
                    colorResource(R.color.theme_cherry_dark_3),
                    colorResource(R.color.theme_cherry_dark_4),
                    colorResource(R.color.theme_cherry_dark_5),
                    colorResource(R.color.theme_cherry_dark_6),
                    colorResource(R.color.theme_cherry_dark_7),
                    colorResource(R.color.theme_cherry_dark_8),
                )

                ICE.code -> listOf(
                    colorResource(R.color.theme_ice_dark_0),
                    colorResource(R.color.theme_ice_dark_1),
                    colorResource(R.color.theme_ice_dark_2),
                    colorResource(R.color.theme_ice_dark_3),
                    colorResource(R.color.theme_ice_dark_4),
                    colorResource(R.color.theme_ice_dark_5),
                    colorResource(R.color.theme_ice_dark_6),
                    colorResource(R.color.theme_ice_dark_7),
                    colorResource(R.color.theme_ice_dark_8),
                )

                GRASS.code -> listOf(
                    colorResource(R.color.theme_grass_dark_0),
                    colorResource(R.color.theme_grass_dark_1),
                    colorResource(R.color.theme_grass_dark_2),
                    colorResource(R.color.theme_grass_dark_3),
                    colorResource(R.color.theme_grass_dark_4),
                    colorResource(R.color.theme_grass_dark_5),
                    colorResource(R.color.theme_grass_dark_6),
                    colorResource(R.color.theme_grass_dark_7),
                    colorResource(R.color.theme_grass_dark_8),
                )

                else -> listOf(
                    colorResource(R.color.theme_snutt_dark_0),
                    colorResource(R.color.theme_snutt_dark_1),
                    colorResource(R.color.theme_snutt_dark_2),
                    colorResource(R.color.theme_snutt_dark_3),
                    colorResource(R.color.theme_snutt_dark_4),
                    colorResource(R.color.theme_snutt_dark_5),
                    colorResource(R.color.theme_snutt_dark_6),
                    colorResource(R.color.theme_snutt_dark_7),
                    colorResource(R.color.theme_snutt_dark_8),
                )
            }[colorIndex.toInt() - 1]
        } else {
            when (code) {
                SNUTT.code -> listOf(
                    colorResource(R.color.theme_snutt_0),
                    colorResource(R.color.theme_snutt_1),
                    colorResource(R.color.theme_snutt_2),
                    colorResource(R.color.theme_snutt_3),
                    colorResource(R.color.theme_snutt_4),
                    colorResource(R.color.theme_snutt_5),
                    colorResource(R.color.theme_snutt_6),
                    colorResource(R.color.theme_snutt_7),
                    colorResource(R.color.theme_snutt_8),
                )

                MODERN.code -> listOf(
                    colorResource(R.color.theme_modern_0),
                    colorResource(R.color.theme_modern_1),
                    colorResource(R.color.theme_modern_2),
                    colorResource(R.color.theme_modern_3),
                    colorResource(R.color.theme_modern_4),
                    colorResource(R.color.theme_modern_5),
                    colorResource(R.color.theme_modern_6),
                    colorResource(R.color.theme_modern_7),
                    colorResource(R.color.theme_modern_8),
                )

                AUTUMN.code -> listOf(
                    colorResource(R.color.theme_autumn_0),
                    colorResource(R.color.theme_autumn_1),
                    colorResource(R.color.theme_autumn_2),
                    colorResource(R.color.theme_autumn_3),
                    colorResource(R.color.theme_autumn_4),
                    colorResource(R.color.theme_autumn_5),
                    colorResource(R.color.theme_autumn_6),
                    colorResource(R.color.theme_autumn_7),
                    colorResource(R.color.theme_autumn_8),
                )

                CHERRY.code -> listOf(
                    colorResource(R.color.theme_cherry_0),
                    colorResource(R.color.theme_cherry_1),
                    colorResource(R.color.theme_cherry_2),
                    colorResource(R.color.theme_cherry_3),
                    colorResource(R.color.theme_cherry_4),
                    colorResource(R.color.theme_cherry_5),
                    colorResource(R.color.theme_cherry_6),
                    colorResource(R.color.theme_cherry_7),
                    colorResource(R.color.theme_cherry_8),
                )

                ICE.code -> listOf(
                    colorResource(R.color.theme_ice_0),
                    colorResource(R.color.theme_ice_1),
                    colorResource(R.color.theme_ice_2),
                    colorResource(R.color.theme_ice_3),
                    colorResource(R.color.theme_ice_4),
                    colorResource(R.color.theme_ice_5),
                    colorResource(R.color.theme_ice_6),
                    colorResource(R.color.theme_ice_7),
                    colorResource(R.color.theme_ice_8),
                )

                GRASS.code -> listOf(
                    colorResource(R.color.theme_grass_0),
                    colorResource(R.color.theme_grass_1),
                    colorResource(R.color.theme_grass_2),
                    colorResource(R.color.theme_grass_3),
                    colorResource(R.color.theme_grass_4),
                    colorResource(R.color.theme_grass_5),
                    colorResource(R.color.theme_grass_6),
                    colorResource(R.color.theme_grass_7),
                    colorResource(R.color.theme_grass_8),
                )

                else -> listOf(
                    colorResource(R.color.theme_snutt_0),
                    colorResource(R.color.theme_snutt_1),
                    colorResource(R.color.theme_snutt_2),
                    colorResource(R.color.theme_snutt_3),
                    colorResource(R.color.theme_snutt_4),
                    colorResource(R.color.theme_snutt_5),
                    colorResource(R.color.theme_snutt_6),
                    colorResource(R.color.theme_snutt_7),
                    colorResource(R.color.theme_snutt_8),
                )
            }[colorIndex.toInt() - 1]
        }
    }
}
