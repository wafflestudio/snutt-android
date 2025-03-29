package com.wafflestudio.snutt2.domainmodel

import androidx.compose.ui.graphics.Color

interface LectureColor {
    val foreground: Color
    val background: Color
}

data class CustomColor(
    override val foreground: Color,
    override val background: Color,
) : LectureColor

data class BuiltInColor(
    override val foreground: Color = Color(0xFFFFFFFF),
    override val background: Color,
    val colorIndex: Int,
) : LectureColor
