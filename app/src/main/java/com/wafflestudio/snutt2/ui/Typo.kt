package com.wafflestudio.snutt2.ui

import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.wafflestudio.snutt2.R

private val Pretendard = FontFamily(
    Font(R.font.pretendard_thin, FontWeight.Thin),
    Font(R.font.pretendard_extralight, FontWeight.ExtraLight),
    Font(R.font.pretendard_light, FontWeight.Light),
    Font(R.font.pretendard_regular, FontWeight.Normal),
    Font(R.font.pretendard_medium, FontWeight.Medium),
    Font(R.font.pretendard_semibold, FontWeight.SemiBold),
    Font(R.font.pretendard_bold, FontWeight.Bold),
    Font(R.font.pretendard_extrabold, FontWeight.ExtraBold),
    Font(R.font.pretendard_black, FontWeight.Black),
)

val SNUTTTypography @Composable get() = Typography(
    defaultFontFamily = Pretendard,
    h1 = TextStyle(
        color = SNUTTColors.Black900,
        fontSize = 22.sp,
        fontFamily = Pretendard,
        fontWeight = FontWeight.Bold,
    ),
    h2 = TextStyle(
        color = SNUTTColors.Black900,
        fontSize = 18.sp,
        fontFamily = Pretendard,
        fontWeight = FontWeight.Bold,
    ),
    h3 = TextStyle(
        color = SNUTTColors.Black900,
        fontSize = 16.sp,
        fontFamily = Pretendard,
        fontWeight = FontWeight.Bold,
    ),
    h4 = TextStyle(
        color = SNUTTColors.Black900,
        fontSize = 14.sp,
        fontFamily = Pretendard,
        fontWeight = FontWeight.Bold,
    ),
    h5 = TextStyle(
        color = SNUTTColors.Black900,
        fontSize = 12.sp,
        fontFamily = Pretendard,
        fontWeight = FontWeight.Bold,
    ),
    subtitle1 = TextStyle(
        color = SNUTTColors.Gray200,
        fontSize = 17.sp,
        fontFamily = Pretendard,
        fontWeight = FontWeight.Medium,
    ),
    subtitle2 = TextStyle(
        color = SNUTTColors.Gray200,
        fontSize = 14.sp,
        fontFamily = Pretendard,
        fontWeight = FontWeight.Medium,
    ),
    button = TextStyle(
        color = SNUTTColors.Black900,
        fontSize = 16.sp,
        fontFamily = Pretendard,
    ),
    body1 = TextStyle(
        color = SNUTTColors.Black900,
        fontSize = 14.sp,
        fontFamily = Pretendard,
    ),
    body2 = TextStyle(
        color = SNUTTColors.Black900,
        fontSize = 12.sp,
        fontFamily = Pretendard,
    ),
)
