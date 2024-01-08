package com.wafflestudio.snutt2.components.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTheme
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.ui.isDarkMode

@Composable
fun SimpleTopBar(
    modifier: Modifier = Modifier,
    title: String,
    onClickNavigateBack: () -> Unit,
) {
    TopBar(
        modifier = modifier,
        title = {
            Text(
                text = title,
                style = SNUTTTypography.h2,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        navigationIcon = {
            ArrowBackIcon(
                modifier = Modifier.clicks(1000L) { onClickNavigateBack() },
                colorFilter = ColorFilter.tint(SNUTTColors.Black900),
            )
        },
    )
}

@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    title: @Composable RowScope.() -> Unit = {},
    navigationIcon: @Composable RowScope.() -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
    ) {
        Row(
            modifier = Modifier
                .background(SNUTTColors.White900)
                .weight(1f)
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            navigationIcon()
            Row(
                modifier = Modifier
                    .weight(1f),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                title()
            }
            actions()
        }
        Divider(
            thickness = 0.5.dp,
            color = SNUTTColors.TableGrid,
        )
    }
}

@Composable
fun CenteredTopBar(
    modifier: Modifier = Modifier,
    title: @Composable RowScope.() -> Unit = {},
    navigationIcon: @Composable RowScope.() -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
) {
    val isDarkMode = isDarkMode()
    Box(
        modifier = modifier
            .background(color = MaterialTheme.colors.primary)
            .fillMaxWidth()
            .height(56.dp)
            .drawWithCache {
                onDrawWithContent {
                    drawLine(
                        color = if (isDarkMode) SNUTTColors.DarkerGray else SNUTTColors.Gray10,
                        start = Offset(0f, this.size.height), end = Offset(this.size.width, this.size.height),
                        strokeWidth = 1f,
                    )
                    drawContent()
                }
            }
            .padding(horizontal = 20.dp),
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.CenterStart),
            horizontalArrangement = Arrangement.Center,
        ) { navigationIcon() }

        Row(
            modifier = Modifier
                .align(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically,
        ) { title() }

        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) { actions() }
    }
}

@Preview
@Composable
fun TopBarPreview() {
    Column {
        TopBar(
            title = {
                Text(
                    text = "나의 시간표",
                    modifier = Modifier.padding(end = 2.dp),
                    style = SNUTTTypography.h2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "(22 학점)",
                    style = SNUTTTypography.subtitle1,
                    maxLines = 1,
                )
            },
            navigationIcon = {
                DrawerIcon()
            },
            actions = {
                ListIcon(Modifier.padding(end = 8.dp))
                ShareIcon(Modifier.padding(end = 8.dp))
                NotificationIcon(Modifier.padding(end = 12.dp), colorFilter = ColorFilter.tint(SNUTTColors.Black900))
            },
        )
        Box(modifier = Modifier.weight(1f))
    }
}

@Preview
@Composable
fun SimpleTopBarPreview() {
    SNUTTTheme {
        Column {
            SimpleTopBar(title = "강의 상세보기", onClickNavigateBack = {})

            Box(
                modifier = Modifier.weight(1f),
            )
        }
    }
}
