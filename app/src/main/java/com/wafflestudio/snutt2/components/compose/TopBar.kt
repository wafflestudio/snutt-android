package com.wafflestudio.snutt2.components.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTheme
import com.wafflestudio.snutt2.ui.SNUTTTypography

@Composable
fun SimpleTopBar(
    modifier: Modifier = Modifier,
    title: String,
    onClickNavigateBack: () -> Unit
) {
    TopBar(
        modifier = modifier,
        title = {
            Text(
                text = title,
                style = SNUTTTypography.h2,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            ArrowBackIcon(
                modifier = Modifier.clicks(1000L) { onClickNavigateBack() },
                colorFilter = ColorFilter.tint(SNUTTColors.Black900),
            )
        }
    )
}

@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    title: @Composable RowScope.() -> Unit = {},
    navigationIcon: @Composable RowScope.() -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {}
) {
    Surface(
        shape = RectangleShape,
        color = SNUTTColors.White900,
        elevation = 1.dp,
        modifier = Modifier
            .padding(bottom = 2.dp)
            .fillMaxWidth()
            .height(56.dp)
            .drawWithContent {
                val paddingPx = 2.dp.toPx()
                clipRect(
                    left = 0f,
                    top = 0f,
                    right = size.width,
                    bottom = size.height + paddingPx
                ) {
                    this@drawWithContent.drawContent()
                }
            }
    ) {
        Row(
            modifier = modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.width(54.dp),
                horizontalArrangement = Arrangement.Center
            ) { navigationIcon() }

            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) { title() }

            Row(
                modifier = Modifier.wrapContentWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) { actions() }
        }
    }
}

@Composable
fun SearchTopBar(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = modifier
            .background(SNUTTColors.White900)
            .shadow(elevation = 1.dp, clip = false)
            .height(56.dp)
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .background(SNUTTColors.Gray100, shape = RoundedCornerShape(6.dp))
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            content()
        }
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
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "(22 학점)",
                    style = SNUTTTypography.subtitle1,
                    maxLines = 1
                )
            },
            navigationIcon = {
                DrawerIcon()
            },
            actions = {
                ListIcon(Modifier.padding(end = 8.dp))
                ShareIcon(Modifier.padding(end = 8.dp))
                NotificationIcon(Modifier.padding(end = 12.dp), colorFilter = ColorFilter.tint(SNUTTColors.Black900))
            }
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
                modifier = Modifier.weight(1f)
            )
        }
    }
}
