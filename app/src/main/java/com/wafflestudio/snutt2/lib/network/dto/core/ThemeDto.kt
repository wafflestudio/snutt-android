package com.wafflestudio.snutt2.lib.network.dto.core

import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.model.BuiltInTheme
import com.wafflestudio.snutt2.model.CustomTheme
import com.wafflestudio.snutt2.model.TableTheme

@JsonClass(generateAdapter = true)
data class ThemeDto(
    val id: String?,
    val theme: Int?,
    val name: String?,
    val colors: List<ColorDto>?,
    val isCustom: Boolean?,
) {

    fun toTableTheme(): TableTheme {
        return if (isCustom != false) {
            CustomTheme(
                id = id!!,
                name = name ?: "",
                colors = colors ?: emptyList(),
            )
        } else {
            BuiltInTheme(
                code = theme ?: 0,
                name = name ?: "",
            )
        }
    }
}
