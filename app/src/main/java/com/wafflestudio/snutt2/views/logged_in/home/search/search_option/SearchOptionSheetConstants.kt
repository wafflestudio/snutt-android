package com.wafflestudio.snutt2.views.logged_in.home.search.search_option

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.lib.network.dto.core.ColorDto
import com.wafflestudio.snutt2.ui.isDarkMode

object SearchOptionSheetConstants {
    const val TagColumnWidthDp = 120
    const val MaxHeightRatio = 0.85f
    val TopMargin = 50.dp
    val AnimationSpec = spring(
        visibilityThreshold = 1f,
        stiffness = 200f,
    )

    @ColorInt val BackgroundLectureBlockColor = Color.argb(153, 27, 208, 200)
    val TimeBlockColor: ColorDto
        @Composable get() = if (isDarkMode()) {
            ColorDto(
                fgRaw = "#777777",
                bgRaw = "#B3505050",
            )
        } else {
            ColorDto(
                fgRaw = "#FFFFFF",
                bgRaw = "#B3DADADA",
            )
        }
}
