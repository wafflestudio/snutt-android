package com.wafflestudio.snutt2.ui.components.compose

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography

@Composable
fun IOSStyleTopBar(
    modifier: Modifier = Modifier,
    title: String,
    backButtonText: String,
    onBack: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(color = SNUTTColors.White900)
            .height(40.dp)
            .drawWithCache {
                onDrawWithContent {
                    drawLine(
                        color = SNUTTColors.EditTextHint,
                        start = Offset(0f, this.size.height),
                        end = Offset(this.size.width, this.size.height),
                        strokeWidth = 0.5.dp.toPx(),
                    )
                    drawContent()
                }
            }
            .padding(horizontal = 5.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Row(
            modifier = Modifier.clicks(1000L) { onBack() },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ArrowBackIcon(
                colorFilter = ColorFilter.tint(SNUTTColors.Black900),
            )
            AnimatedContent(backButtonText, label = "") {
                Text(
                    text = it,
                    style = SNUTTTypography.button,
                )
            }
        }
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = title,
                style = SNUTTTypography.h3,
            )
        }
    }
}

@Preview
@Composable
fun IOSStyleTopBarPreview() {
    IOSStyleTopBar(
        title = "비밀번호 재설정",
        backButtonText = "로그인",
    ) { }
}
