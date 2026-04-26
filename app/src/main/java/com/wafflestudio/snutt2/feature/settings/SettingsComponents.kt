package com.wafflestudio.snutt2.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.snutt2.ui.components.compose.PersonIcon
import com.wafflestudio.snutt2.ui.components.compose.RedDotWithNumber
import com.wafflestudio.snutt2.ui.components.compose.RightArrowIcon
import com.wafflestudio.snutt2.ui.components.compose.clicks
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewSurface
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography
import com.wafflestudio.snutt2.ui.theme.onSurfaceVariant

@Composable
fun SettingColumn(
    modifier: Modifier = Modifier,
    title: String = "",
    titleStyle: TextStyle = SNUTTTypography.body2.copy(
        color = MaterialTheme.colors.onSurfaceVariant,
    ),
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (title.isNotEmpty()) {
            Text(
                text = title,
                modifier = Modifier
                    .padding(top = 24.dp, bottom = 8.dp, start = 20.dp)
                    .align(Alignment.Start),
                style = titleStyle,
            )
            Spacer(modifier = Modifier.size(5.dp))
        }
        content()
    }
}

@Composable
fun SettingItem(
    title: String,
    modifier: Modifier = Modifier,
    titleColor: Color = MaterialTheme.colors.onSurface,
    leadingIcon: @Composable () -> Unit = {},
    hasNextPage: Boolean = true,
    settingPageNewBadgeTitles: List<String> = emptyList(),
    redDotIconNumber: Long? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(45.dp)
            .background(MaterialTheme.colors.surface)
            .clicks { if (onClick != null) onClick() }
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        leadingIcon()
        Text(
            text = title,
            style = SNUTTTypography.body1.copy(
                color = titleColor,
            ),
        )
        if (redDotIconNumber != null && redDotIconNumber > 0) {
            RedDotWithNumber(Modifier.padding(start = 8.dp), redDotIconNumber)
        }
        if (settingPageNewBadgeTitles.contains(title)) {
            NewBadge(Modifier.padding(start = 5.dp))
        }
        Spacer(modifier = Modifier.weight(1f))
        content()
        if (hasNextPage) {
            RightArrowIcon(
                modifier = Modifier.size(22.dp),
                colorFilter = ColorFilter.tint(SNUTTColors.Black500),
            )
        }
    }
}

@Composable
fun NewBadge(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(width = 26.dp, height = 14.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(SNUTTColors.SNUTTTheme),
    ) {
        Text(
            text = "NEW!",
            modifier = Modifier.align(Alignment.Center),
            style = SNUTTTypography.body2
                .copy(
                    color = SNUTTColors.AllWhite,
                    fontSize = 7.sp,
                    fontWeight = FontWeight.SemiBold,
                ),
        )
    }
}

@SnuttPreview
@Composable
private fun SettingItem_TitleOnly() {
    SnuttPreviewSurface {
        SettingItem(
            title = "알림",
            onClick = {},
        )
    }
}

@SnuttPreview
@Composable
private fun SettingItem_WithLeadingIconAndContent() {
    SnuttPreviewSurface {
        SettingItem(
            title = "내 계정",
            leadingIcon = {
                PersonIcon(
                    modifier = Modifier
                        .size(22.dp)
                        .padding(end = 5.dp),
                )
            },
            onClick = {},
            content = {
                Text(
                    text = "양주현",
                    style = SNUTTTypography.body1.copy(color = SNUTTColors.Black500),
                )
            },
        )
    }
}

@SnuttPreview
@Composable
private fun SettingItem_WithRedDotAndNewBadge() {
    SnuttPreviewSurface {
        SettingItem(
            title = "빈자리 알림",
            settingPageNewBadgeTitles = listOf("빈자리 알림"),
            redDotIconNumber = 3L,
            onClick = {},
        )
    }
}

@SnuttPreview
@Composable
private fun SettingItem_LogoutStyle() {
    SnuttPreviewSurface {
        SettingItem(
            title = "로그아웃",
            titleColor = SNUTTColors.Red,
            onClick = {},
        )
    }
}

@SnuttPreview
@Composable
private fun SettingColumn_WithTitle() {
    SnuttPreviewSurface {
        SettingColumn(
            title = "테마 설정",
        ) {
            SettingItem(title = "색상 모드 선택", onClick = {})
            SettingItem(title = "시간표 설정", onClick = {})
        }
    }
}

@SnuttPreview
@Composable
private fun NewBadge_Default() {
    SnuttPreviewSurface {
        NewBadge()
    }
}
