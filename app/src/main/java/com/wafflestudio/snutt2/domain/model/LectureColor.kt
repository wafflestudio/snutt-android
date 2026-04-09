package com.wafflestudio.snutt2.domain.model

import android.os.Parcelable
import androidx.compose.ui.graphics.toArgb
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
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
