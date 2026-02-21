package com.wafflestudio.snutt2.lib.network.dto.core

import com.squareup.moshi.JsonClass
import com.wafflestudio.snutt2.domainmodel.BuiltInTheme
import com.wafflestudio.snutt2.domainmodel.CustomTheme
import com.wafflestudio.snutt2.domainmodel.TableTheme

@JsonClass(generateAdapter = true)
data class ThemeDto(
    val id: String?,
    val theme: Int?,
    val name: String?,
    val colors: List<ColorDto>?,
    val isCustom: Boolean?,
    val status: String?,
) {

    fun toTableTheme(): TableTheme {
        return if (isCustom != false) {
            CustomTheme(
                id = id!!,
                name = name ?: "",
                colors = colors?.map { it.toThemeColor() } ?: emptyList(),
                isFromMarket = status == "DOWNLOADED",
            )
        } else {
            BuiltInTheme.fromCode(theme ?: 0)
        }
    }
}
