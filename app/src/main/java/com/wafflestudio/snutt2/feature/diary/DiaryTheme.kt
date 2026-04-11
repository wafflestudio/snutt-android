package com.wafflestudio.snutt2.feature.diary

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.wafflestudio.snutt2.ui.theme.isDarkMode

@Immutable
data class DiaryColors(
    // 배경
    val screenBackground: Color,
    val headerBackground: Color,
    val bodyBackground: Color,
    val cardBackground: Color,
    val summaryCardBackground: Color,

    // 텍스트
    val textPrimary: Color,
    val textSecondary: Color,
    val textLabel: Color,
    val textBody: Color,
    val textSubtitle: Color,

    // 악센트
    val accent: Color,
    val accentStrong: Color,
    val theme: Color,

    // 옵션 선택 (질문 답변 pill)
    val optionSelectedText: Color,
    val optionSelectedBorder: Color,
    val optionSelectedBackground: Color,
    val optionUnselectedText: Color,
    val optionUnselectedBorder: Color,

    // 제출 버튼
    val submitButtonEnabled: Color,
    val submitButtonDisabled: Color,
    val submitButtonTextEnabled: Color,
    val submitButtonTextDisabled: Color,

    // 학기 필터 pill
    val filterPillSelectedBackground: Color,
    val filterPillSelectedText: Color,
    val filterPillUnselectedBackground: Color,
    val filterPillUnselectedText: Color,

    // 구분선 / 테두리
    val headerBorder: Color,
    val divider: Color,
    val sectionDivider: Color,
    val nextActionBorder: Color,

    // 아이콘
    val iconPrimary: Color,
    val iconSecondary: Color,
    val exitIcon: Color,
)

// TODO: 다크모드 색상 확정 후 DarkDiaryColors 값 수정
val LightDiaryColors = DiaryColors(
    screenBackground = Color(0xffffffff),
    headerBackground = Color(0xffffffff),
    bodyBackground = Color(0xfff2f2f2),
    cardBackground = Color(0xffffffff),
    summaryCardBackground = Color(0xfff7f7f7),

    textPrimary = Color(0xff000000),
    textSecondary = Color(0xff8a898e),
    textLabel = Color(0xff8a898e),
    textBody = Color(0xff505050),
    textSubtitle = Color(0x80000000),

    accent = Color(0xff1bd0c8),
    accentStrong = Color(0xff00b8b0),
    theme = Color(0xff1BD0C8),

    optionSelectedText = Color(0xff1ca6a0),
    optionSelectedBorder = Color(0xff1bd0c8),
    optionSelectedBackground = Color(0x0f1bd0c8),
    optionUnselectedText = Color(0xff505050),
    optionUnselectedBorder = Color(0xffebebed),

    submitButtonEnabled = Color(0xff1bd0c8),
    submitButtonDisabled = Color(0xffebebeb),
    submitButtonTextEnabled = Color(0xffffffff),
    submitButtonTextDisabled = Color(0xffcacaca),

    filterPillSelectedBackground = Color(0xff1BD0C8),
    filterPillSelectedText = Color(0xffffffff),
    filterPillUnselectedBackground = Color(0xfff7f7f7),
    filterPillUnselectedText = Color(0xff8a898e),

    headerBorder = Color(0xffdcdcde),
    divider = Color(0xfff2f2f2),
    sectionDivider = Color(0x26000000),
    nextActionBorder = Color(0xfff2f2f2),

    iconPrimary = Color(0xff000000),
    iconSecondary = Color(0xffc4c4c4),
    exitIcon = Color(0xffa6a6a6),
)

val DarkDiaryColors = DiaryColors(
    screenBackground = Color(0xff1a1a1a),
    headerBackground = Color(0xff2b2b2b),
    bodyBackground = Color(0xff1a1a1a),
    cardBackground = Color(0xff2c2c2c),
    summaryCardBackground = Color(0xff2c2c2c),

    textPrimary = Color(0xffffffff),
    textSecondary = Color(0xffa6a6a6),
    textLabel = Color(0xffc4c4c4),
    textBody = Color(0xffc4c4c4),
    textSubtitle = Color(0x80ffffff),

    accent = Color(0xff58c1b7),
    accentStrong = Color(0xff58c1b7),
    theme = Color(0xff58c1b7),

    optionSelectedText = Color(0xff00b8b0),
    optionSelectedBorder = Color(0xff1ca6a0),
    optionSelectedBackground = Color(0x141ca6a0),
    optionUnselectedText = Color(0xffc4c4c4),
    optionUnselectedBorder = Color(0xcca6a6a6),

    submitButtonEnabled = Color(0xff58c1b7),
    submitButtonDisabled = Color(0xff3a3a3a),
    submitButtonTextEnabled = Color(0xffffffff),
    submitButtonTextDisabled = Color(0xff666666),

    filterPillSelectedBackground = Color(0xff00b8b0),
    filterPillSelectedText = Color(0xffffffff),
    filterPillUnselectedBackground = Color(0xff2c2c2c),
    filterPillUnselectedText = Color(0xff8a898e),

    headerBorder = Color(0xff333333),
    divider = Color(0xff3c3c3c),
    sectionDivider = Color(0xff333333),
    nextActionBorder = Color(0xff505050),

    iconPrimary = Color(0xffffffff),
    iconSecondary = Color(0xff666666),
    exitIcon = Color(0xffa6a6a6),
)

val LocalDiaryColors = staticCompositionLocalOf { LightDiaryColors }

object DiaryTheme {
    val colors: DiaryColors
        @Composable get() = LocalDiaryColors.current
}

@Composable
fun DiaryTheme(
    darkTheme: Boolean = isDarkMode(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) DarkDiaryColors else LightDiaryColors
    CompositionLocalProvider(LocalDiaryColors provides colors) {
        content()
    }
}
