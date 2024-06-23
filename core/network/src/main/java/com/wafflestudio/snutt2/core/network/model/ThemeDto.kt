package com.wafflestudio.snutt2.core.network.model

import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.model.BuiltInTheme
import com.wafflestudio.snutt2.model.CustomTheme
import com.wafflestudio.snutt2.model.TableTheme

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
