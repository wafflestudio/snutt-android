package com.wafflestudio.snutt2.lib.network.dto.core

import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.model.BuiltInTheme
import com.wafflestudio.snutt2.model.CustomTheme
import com.wafflestudio.snutt2.model.TableTheme
import com.wafflestudio.snutt2.core.network.model.BuiltInTheme as BuiltInThemeNetwork
import com.wafflestudio.snutt2.core.network.model.CustomTheme as CustomThemeNetwork
import com.wafflestudio.snutt2.core.network.model.ThemeDto as ThemeDtoNetwork

@JsonClass(generateAdapter = true)
data class ThemeDto(
    val id: String? = null,
    val theme: Int = 0,
    val name: String = "",
    val colors: List<ColorDto> = emptyList(),
    val isCustom: Boolean = false,
) {

    fun toTableTheme(): TableTheme {
        return if (isCustom) {
            CustomTheme(
                id = id!!,
                name = name,
                colors = colors,
            )
        } else {
            BuiltInTheme(
                code = theme,
                name = name,
            )
        }
    }
}


fun CustomThemeNetwork.toExternalModel() = CustomTheme(
    id = this.id,
    name = this.name,
    colors = this.colors.map { it.toExternalModel() },
)

fun BuiltInThemeNetwork.toExternalModel() = BuiltInTheme(
    code = this.code,
    name = this.name,
)

fun ThemeDtoNetwork.toExternalModel() = ThemeDto(
    id = this.id,
    theme = this.theme,
    name = this.name,
    colors = this.colors.map { it.toExternalModel() },
    isCustom = this.isCustom,
)