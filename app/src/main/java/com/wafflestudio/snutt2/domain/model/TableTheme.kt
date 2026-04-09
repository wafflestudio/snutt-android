package com.wafflestudio.snutt2.domain.model

import androidx.compose.ui.graphics.toArgb
import com.wafflestudio.snutt2.lib.Selectable
import com.wafflestudio.snutt2.ui.SNUTTColors

sealed class TableTheme(
    open val name: String,
    private val lightColors: List<ThemeColor>,
    private val darkColors: List<ThemeColor>,
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as TableTheme
        if (name != other.name) return false
        if (lightColors != other.lightColors) return false
        if (darkColors != other.darkColors) return false

        return when {
            this is CustomTheme && other is CustomTheme -> this.id == other.id
            this is BuiltInTheme && other is BuiltInTheme -> this.code == other.code
            else -> false
        }
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + lightColors.hashCode()
        result = 31 * result + darkColors.hashCode()
        result = 31 * result + when (this) {
            is CustomTheme -> id.hashCode()
            is BuiltInTheme -> code.hashCode()
        }
        return result
    }
}

class CustomTheme(
    val id: String,
    override val name: String,
    val isFromMarket: Boolean,
    colors: List<ThemeColor>,
) : TableTheme(
    name = name,
    lightColors = colors,
    darkColors = colors,
) {
    fun getColors(): List<ThemeColor> = getColors(false)

    companion object {
        // FIXME: 다국어 대응 미완료. "새 테마"가 영어 로케일에서도 그대로 노출됨.
        //  Default가 색상 템플릿과 UI 표시용 기본 이름을 동시에 갖고 있어서,
        //  name을 리소스화하려면 Context가 필요한데 도메인 모델에서는 접근할 수 없음.
        //  새 테마 생성 시 초기값 + ThemeRepositoryImpl.getTheme() fallback 경로에서 노출되나,
        //  사용자가 바로 수정하는 값이고 fallback은 에러 케이스이므로 현재는 감수.
        val Default = CustomTheme(
            id = "",
            name = "새 테마",
            isFromMarket = false,
            colors = listOf(
                ThemeColor(
                    foreground = SNUTTColors.White.toArgb(),
                    background = SNUTTColors.MainBlue.toArgb(),
                ),
            ),
        )
    }
}

class BuiltInTheme(
    val code: Int,
    override val name: String,
    lightColors: List<ThemeColor>,
    darkColors: List<ThemeColor>,
) : TableTheme(
    name = name,
    lightColors = lightColors,
    darkColors = darkColors,
) {
    companion object {
        val SNUTT = BuiltInTheme(
            code = 0,
            name = "SNUTT",
            lightColors = listOf(
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFFE54459.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFFF58D3D.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFFFAC42D.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFFA6D930.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFF2BC267.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFF1BD0C8.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFF1D99E8.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFF4F48C4.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFFAF56B3.toInt()),
            ),
            darkColors = listOf(
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFFD95F71.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFFDF6E3C.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFFE68937.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFF95B03E.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFF419343.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFF5BA0D7.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFF58C1B7.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFF3E35A7.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFF783891.toInt()),
            ),
        )
        val MODERN = BuiltInTheme(
            code = 1,
            name = "모던",
            lightColors = listOf(
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFFF0652A.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFFF5AD3E.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFF998F36.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFF89C291.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFF266F55.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFF13808F.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFF366689.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFF432920.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFFD82F3D.toInt()),
            ),
            darkColors = listOf(
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFFBB592F.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFFE08B45.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFFB4B194.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFF5B967C.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFF266F55.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFF13808F.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFF426586.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFF5C4335.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFFAD2F31.toInt()),
            ),
        )
        val AUTUMN = BuiltInTheme(
            code = 2,
            name = "가을",
            lightColors = listOf(
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFFB82E31.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFFDB701C.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFFEAA32A.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFFC6C013.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFF3A856E.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFF19B2AC.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFF3994CE.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFF3F3A9C.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFF924396.toInt()),
            ),
            darkColors = listOf(
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFFA93A36.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFFD56738.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFFCC973F.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFFA0942F.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFF4E8370.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFF29625A.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFF4171A2.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFF4F48C4.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFF783891.toInt()),
            ),
        )
        val CHERRY = BuiltInTheme(
            code = 3,
            name = "벚꽃",
            lightColors = listOf(
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFFFD79A8.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFFFEC9DD.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFFFEB0CC.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFFFE93BF.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFFE9B1D0.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFFC67D97.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFFBB8EA7.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFFBDB4BF.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFFE16597.toInt()),
            ),
            darkColors = listOf(
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFFA43C58.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFF7C164F.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFF99446E.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFFA77085.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFFB290B8.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFFBDB4BF.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFFBB8EA7.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFF736C75.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFFC76F92.toInt()),
            ),
        )
        val ICE = BuiltInTheme(
            code = 4,
            name = "얼음",
            lightColors = listOf(
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFFAABDCF.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFFC0E9E8.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFF66B6CA.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFF015F95.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFFA8D0DB.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFF458ED0.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFF62A9D1.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFF20363D.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFF6D8A96.toInt()),
            ),
            darkColors = listOf(
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFF014D79.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFF788DA4.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFFAEC1C9.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFF48595B.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFF1C6C8E.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFF64909C.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFF88B1C6.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFF44576B.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFF757C80.toInt()),
            ),
        )
        val GRASS = BuiltInTheme(
            code = 5,
            name = "잔디",
            lightColors = listOf(
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFF4FBEAA.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFF9FC1A4.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFF5A8173.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFF84AEB1.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFF266F55.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFFD0E0C4.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFF59886D.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFF476060.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFF3D7068.toInt()),
            ),
            darkColors = listOf(
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFF2D5A45.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFF429587.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFF86A99A.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFF597B6A.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFF42635B.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFF586C5D.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFF324845.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFFAAB6B1.toInt()),
                ThemeColor(foreground = 0xFFFFFFFF.toInt(), background = 0xFF747877.toInt()),
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
    val colors: List<Selectable<ThemeColor>>,
    private val originalTheme: TableTheme,
) {
    val isEditable get() = originalTheme.isEditable
    val isNew get() = originalTheme.isNew
    val isCustomTheme get() = originalTheme is CustomTheme
    val isFromMarket get() = originalTheme is CustomTheme && originalTheme.isFromMarket
    val canAddColor get() = isEditable && colors.size < 9
    val canRemoveColor get() = isEditable && colors.size > 1
    val canDuplicateColor get() = canAddColor

    fun getDisplayColors(isDarkMode: Boolean): List<Selectable<ThemeColor>> {
        return if (isEditable) {
            colors
        } else {
            originalTheme.getColors(isDarkMode).map { Selectable(it, false) }
        }
    }

    fun hasChange(): Boolean {
        return if (originalTheme.isEditable) {
            name != originalTheme.name ||
                colors.map { it.item } != originalTheme.getColors(false)
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
        fun fromTableTheme(tableTheme: TableTheme): EditingTheme {
            return EditingTheme(
                name = tableTheme.name,
                colors = tableTheme.getColors(false).mapIndexed { index, color ->
                    Selectable(color, tableTheme.isEditable && index == 0)
                },
                originalTheme = tableTheme,
            )
        }
    }
}
