package com.wafflestudio.snutt2.domainmodel

import android.os.Parcelable
import androidx.compose.ui.graphics.toArgb
import com.wafflestudio.snutt2.lib.network.dto.core.ColorDto
import com.wafflestudio.snutt2.ui.SNUTTColors
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
sealed interface LectureColor : Parcelable {

    @Serializable
    @Parcelize
    data class Custom(
        val foreground: Int,
        val background: Int,
    ) : LectureColor {
        fun toColorDto(): ColorDto {
            return ColorDto(
                fgColor = foreground,
                bgColor = background,
            )
        }

        companion object {
            val Default = Custom(
                foreground = SNUTTColors.White.toArgb(),
                background = SNUTTColors.MainBlue.toArgb(),
            )
        }
    }

    @Serializable
    @Parcelize
    data class BuiltIn(
        val colorIndex: Int,
    ) : LectureColor
}
