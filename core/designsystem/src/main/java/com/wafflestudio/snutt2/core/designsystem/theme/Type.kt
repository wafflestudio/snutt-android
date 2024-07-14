package com.wafflestudio.snutt2.core.designsystem.theme

import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.wafflestudio.snutt2.core.designsystem.R

private val SpoqaHanSans = FontFamily(
    Font(R.font.spoqa_han_sans_thin, FontWeight.Thin),
    Font(R.font.spoqa_han_sans_light, FontWeight.Light),
    Font(R.font.spoqa_han_sans_regular, FontWeight.Medium),
    Font(R.font.spoqa_han_sans_bold, FontWeight.Bold),
)

val SNUTTTypography @Composable get() = Typography(
    defaultFontFamily = SpoqaHanSans,
    h1 = TextStyle(
        color = Black900,
        fontSize = 22.sp,
        fontFamily = SpoqaHanSans,
        fontWeight = FontWeight.Bold,
    ),
    h2 = TextStyle(
        color = Black900,
        fontSize = 18.sp,
        fontFamily = SpoqaHanSans,
        fontWeight = FontWeight.Bold,
    ),
    h3 = TextStyle(
        color = Black900,
        fontSize = 16.sp,
        fontFamily = SpoqaHanSans,
        fontWeight = FontWeight.Bold,
    ),
    h4 = TextStyle(
        color = Black900,
        fontSize = 14.sp,
        fontFamily = SpoqaHanSans,
        fontWeight = FontWeight.Bold,
    ),
    h5 = TextStyle(
        color = Black900,
        fontSize = 12.sp,
        fontFamily = SpoqaHanSans,
        fontWeight = FontWeight.Bold,
    ),
    subtitle1 = TextStyle(
        color = Gray200,
        fontSize = 17.sp,
        fontFamily = SpoqaHanSans,
        fontWeight = FontWeight.Medium,
    ),
    subtitle2 = TextStyle(
        color = Gray200,
        fontSize = 14.sp,
        fontFamily = SpoqaHanSans,
        fontWeight = FontWeight.Medium,
    ),
    button = TextStyle(
        color = Black900,
        fontSize = 16.sp,
        fontFamily = SpoqaHanSans,
    ),
    body1 = TextStyle(
        color = Black900,
        fontSize = 14.sp,
        fontFamily = SpoqaHanSans,
    ),
    body2 = TextStyle(
        color = Black900,
        fontSize = 12.sp,
        fontFamily = SpoqaHanSans,
    ),
)