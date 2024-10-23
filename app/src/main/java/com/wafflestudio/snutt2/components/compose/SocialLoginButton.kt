package com.wafflestudio.snutt2.components.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun SocialLoginButton(
    painter: Painter,
    size: Dp = 44.dp,
    onClick: () -> Unit,
) {
    Image(
        painter = painter,
        contentDescription = "",
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .clickable {
                onClick()
            },
    )
}
