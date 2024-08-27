package com.wafflestudio.snutt2.views.logged_in.home.timetable

import androidx.compose.ui.text.TextStyle

data class LectureCellInfo(
    val text: String,
    val enabled: Boolean,
    val style: TextStyle,
    val minifiedStyle: TextStyle,
)
