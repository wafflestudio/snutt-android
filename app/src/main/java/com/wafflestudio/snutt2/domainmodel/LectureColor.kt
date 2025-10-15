package com.wafflestudio.snutt2.domainmodel

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.toColorInt
import com.wafflestudio.snutt2.lib.network.dto.core.ColorDto
import com.wafflestudio.snutt2.ui.SNUTTColors

interface LectureColor {
    val foreground: Color
    val background: Color

    fun toColorDto(): ColorDto {
        return ColorDto(
            fgColor = foreground.toArgb(),
            bgColor = background.toArgb(),
        )
    }
}

data class CustomColor(
    override val foreground: Color,
    override val background: Color,
) : LectureColor {
    companion object {
        val Default = CustomColor(
            foreground = SNUTTColors.White,
            background = SNUTTColors.MainBlue,
        )
    }
}

data class BuiltInColor(
    override val foreground: Color = Color(0xFFFFFFFF),
    override val background: Color,
    val colorIndex: Long,
) : LectureColor

fun ColorDto.toCustomColor(): CustomColor {
    return CustomColor(
        foreground = Color(fgRaw?.toColorInt() ?: 0xFFFFFF),
        background = Color(bgRaw?.toColorInt() ?: 0xFFFFFF),
    )
}
