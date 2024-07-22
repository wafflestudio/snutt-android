package com.wafflestudio.snutt2.views.logged_in.home.timetable

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.view.TextRect
import com.wafflestudio.snutt2.components.view.TextRectTemp
import com.wafflestudio.snutt2.lib.rx.dp
import com.wafflestudio.snutt2.lib.rx.sp
import com.wafflestudio.snutt2.ui.isDarkMode

@Stable
object TimetableCanvasObjects {
    val hourLabelWidth @Composable get() = 24.5f.dp(LocalContext.current)
    val dayLabelHeight @Composable get() = 28.5f.dp(LocalContext.current)
    val cellPadding @Composable get() = 4.dp(LocalContext.current)

    val dayLabelTextPaint
        @Composable get() =
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color =
                    if (isDarkMode()) {
                        Color.argb(180, 119, 119, 119)
                    } else {
                        Color.argb(180, 0, 0, 0)
                    }
                textSize = 12.sp(LocalContext.current)
                textAlign = Paint.Align.CENTER
                typeface = ResourcesCompat.getFont(LocalContext.current, R.font.spoqa_han_sans_light)
            }
    val dayLabelTextHeight: Float
        @Composable get() {
            val text = "월"
            val bounds = Rect()
            dayLabelTextPaint.getTextBounds(text, 0, text.length, bounds)
            return bounds.height().toFloat()
        }

    val hourLabelTextPaint
        @Composable get() =
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color =
                    if (isDarkMode()) {
                        Color.argb(180, 119, 119, 119)
                    } else {
                        Color.argb(180, 0, 0, 0)
                    }
                textSize = 12.sp(LocalContext.current)
                textAlign = Paint.Align.RIGHT
                typeface = ResourcesCompat.getFont(LocalContext.current, R.font.spoqa_han_sans_light)
            }

    val lectureCellTextRect
        @Composable get() =
            TextRectTemp(
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    textSize = 11f.sp(LocalContext.current)
                    typeface = ResourcesCompat.getFont(LocalContext.current, R.font.spoqa_han_sans_regular)
                },
            )

    val lectureCellTextRectReduced
        @Composable get() =
            TextRectTemp(
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    textSize = 8.8f.sp(LocalContext.current)
                    typeface = ResourcesCompat.getFont(LocalContext.current, R.font.spoqa_han_sans_regular)
                },
            )

    val lectureCellSubTextRect
        @Composable get() =
            TextRectTemp(
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    textSize = 12f.sp(LocalContext.current)
                    typeface = ResourcesCompat.getFont(LocalContext.current, R.font.spoqa_han_sans_regular)
                },
            )

    val lectureCellSubTextRectReduced
        @Composable get() =
            TextRectTemp(
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    textSize = 9.6f.sp(LocalContext.current)
                    typeface = ResourcesCompat.getFont(LocalContext.current, R.font.spoqa_han_sans_regular)
                },
            )

    val lectureCellSubTextRectBold
        @Composable get() =
            TextRectTemp(
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    textSize = 12f.sp(LocalContext.current)
                    typeface = ResourcesCompat.getFont(LocalContext.current, R.font.spoqa_han_sans_bold)
                },
            )

    val lectureCellSubTextRectReducedBold
        @Composable get() =
            TextRectTemp(
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    textSize = 9.6f.sp(LocalContext.current)
                    typeface = ResourcesCompat.getFont(LocalContext.current, R.font.spoqa_han_sans_bold)
                },
            )

    val lectureCellBorderPaint: Paint =
        Paint().apply {
            style = Paint.Style.STROKE
            color = 0x0d000000
            strokeWidth = 1.dp.value
        }
}
