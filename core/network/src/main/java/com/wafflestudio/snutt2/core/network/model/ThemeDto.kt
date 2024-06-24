package com.wafflestudio.snutt2.core.network.model

import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.model.BuiltInTheme // TODO : 얘는 옮겨오지 못했다. 나중에 처리
import com.wafflestudio.snutt2.model.CustomTheme // TODO : 얘는 옮겨오지 못했다. 나중에 처리
import com.wafflestudio.snutt2.model.TableTheme // TODO : 얘는 옮겨오지 못했다. 나중에 처리

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
