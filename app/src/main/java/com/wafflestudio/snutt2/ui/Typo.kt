package com.wafflestudio.snutt2.ui

import androidx.compose.material.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.wafflestudio.snutt2.R

private val SpoqaHanSans = FontFamily(
    Font(R.font.spoqa_han_sans_thin, FontWeight.Thin),
    Font(R.font.spoqa_han_sans_light, FontWeight.Light),
    Font(R.font.spoqa_han_sans_regular, FontWeight.Medium),
    Font(R.font.spoqa_han_sans_bold, FontWeight.Bold),
)

val SNUTTTypography = Typography(
    defaultFontFamily = SpoqaHanSans,
    h1 = TextStyle(
        color = SNUTTColors.Black,
        fontSize = 22.sp,
        fontFamily = SpoqaHanSans,
        fontWeight = FontWeight.Bold
    ),
    h2 = TextStyle(
        color = SNUTTColors.Black,
        fontSize = 18.sp,
        fontFamily = SpoqaHanSans,
        fontWeight = FontWeight.Bold
    ),
    h3 = TextStyle(
        color = SNUTTColors.Black,
        fontSize = 16.sp,
        fontFamily = SpoqaHanSans,
        fontWeight = FontWeight.Bold
    ),
    h4 = TextStyle(
        color = SNUTTColors.Black,
        fontSize = 14.sp,
        fontFamily = SpoqaHanSans,
        fontWeight = FontWeight.Bold
    ),
    h5 = TextStyle(
        color = SNUTTColors.Black,
        fontSize = 12.sp,
        fontFamily = SpoqaHanSans,
        fontWeight = FontWeight.Bold
    ),
    subtitle1 = TextStyle(
        color = SNUTTColors.Gray400,
        fontSize = 17.sp,
        fontFamily = SpoqaHanSans,
        fontWeight = FontWeight.Medium
    ),
    subtitle2 = TextStyle(
        color = SNUTTColors.Gray400,
        fontSize = 17.sp,
        fontFamily = SpoqaHanSans,
        fontWeight = FontWeight.Medium
    ),
    button = TextStyle(
        color = Color.Black,
        fontSize = 16.sp,
        fontFamily = SpoqaHanSans,
    )
)

// val f = TextStyle(
//    color = Color.Black,
//    fontSize = 14.sp,
//    fontFamily = SpoqaHanSans,
// )
// val g = TextStyle(
//    color = Color.Black,
//    fontSize = 13.sp,
//    fontFamily = SpoqaHanSans,
// )
