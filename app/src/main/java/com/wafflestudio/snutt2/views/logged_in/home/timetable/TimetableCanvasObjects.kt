package com.wafflestudio.snutt2.views.logged_in.home.timetable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.wafflestudio.snutt2.lib.rx.dp

@Stable
object TimetableCanvasObjects {
    val hourLabelWidth @Composable get() = 24.5f.dp(LocalContext.current)
    val dayLabelHeight @Composable get() = 28.5f.dp(LocalContext.current)

    val lectureTitleTextStyle = TextStyle(
        fontSize = 11.sp,
        fontWeight = FontWeight.Normal,
    )
    val lectureTitleMinifiedTextStyle = TextStyle(
        fontSize = 11.sp,
        fontWeight = FontWeight.Normal,
    )

    val lecturePlaceTextStyle = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
    )
    val lecturePlaceMinifiedTextStyle = TextStyle(
        fontSize = 9.6.sp,
        fontWeight = FontWeight.SemiBold,
    )

    val lectureNumberTextStyle = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Normal,
    )
    val lectureNumberMinifiedTextStyle = TextStyle(
        fontSize = 9.6.sp,
        fontWeight = FontWeight.Normal,
    )

    val lectureInstructorTextStyle = TextStyle(
        fontSize = 11.sp,
        fontWeight = FontWeight.Normal,
    )
    val lectureInstructorMinifiedTextStyle = TextStyle(
        fontSize = 8.8.sp,
        fontWeight = FontWeight.Normal,
    )
}
