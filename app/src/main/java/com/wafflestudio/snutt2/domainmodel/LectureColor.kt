package com.wafflestudio.snutt2.domainmodel

import android.os.Parcelable
import com.wafflestudio.snutt2.lib.network.dto.core.ColorDto
import kotlinx.parcelize.Parcelize

sealed interface LectureColor : Parcelable {

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
                foreground = 0xFFFFFFFF.toInt(),
                background = 0xFF1BD0C8.toInt(),
            )
        }
    }

    @Parcelize
    data class BuiltIn(
        val colorIndex: Int,
    ) : LectureColor
}
