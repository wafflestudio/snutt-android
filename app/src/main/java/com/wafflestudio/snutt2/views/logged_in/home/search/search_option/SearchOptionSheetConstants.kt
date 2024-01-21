package com.wafflestudio.snutt2.views.logged_in.home.search.search_option

import androidx.compose.animation.core.spring
import androidx.compose.ui.unit.dp

object SearchOptionSheetConstants {
    const val TagColumnWidthDp = 120
    val SheetTopMargin = 40.dp
    val SearchOptionSheetAnimationSpec = spring(
        visibilityThreshold = 1f,
        stiffness = 200f,
    )
}
