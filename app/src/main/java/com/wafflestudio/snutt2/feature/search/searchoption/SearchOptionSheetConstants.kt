package com.wafflestudio.snutt2.feature.search.searchoption

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.compose.animation.core.spring
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt

object SearchOptionSheetConstants {
    const val TAG_COLUMN_WIDTH_DP = 120
    const val MAX_HEIGHT_RATIO = 0.85f
    val TopMargin = 68.dp
    val AnimationSpec = spring(
        visibilityThreshold = 1f,
        stiffness = 200f,
    )

    @ColorInt
    val BackgroundLectureBlockColor = Color.argb(153, 27, 208, 200)

    @ColorInt
    val TimeBlockFgColorLight = "#FFFFFF".toColorInt()

    @ColorInt
    val TimeBlockFgColorDark = "#777777".toColorInt()

    @ColorInt
    val TimeBlockBgColorLight = "#B3DADADA".toColorInt()

    @ColorInt
    val TimeBlockBgColorDark = "#B3505050".toColorInt()
}
