package com.wafflestudio.snutt2.views.logged_in.home.search.search_option

import androidx.compose.animation.core.spring

object SearchOptionSheetConstants {
    const val TagColumnWidthDp = 120
    val SearchOptionSheetAnimationSpec = spring(
        visibilityThreshold = 1f,
        stiffness = 200f,
    )
}
