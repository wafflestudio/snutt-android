package com.wafflestudio.snutt2.views.logged_in.home.timetable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.platform.LocalContext
import com.wafflestudio.snutt2.lib.rx.dp

@Stable
object TimetableCanvasObjects {
    val hourLabelWidth @Composable get() = 24.5f.dp(LocalContext.current)
    val dayLabelHeight @Composable get() = 28.5f.dp(LocalContext.current)
}
